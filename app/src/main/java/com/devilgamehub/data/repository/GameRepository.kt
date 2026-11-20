package com.devilgamehub.data.repository

import android.content.Context
import com.devilgamehub.data.local.LocalGameProvider
import com.devilgamehub.data.local.SettingsRepository
import com.devilgamehub.data.model.Game
import com.devilgamehub.data.remote.RetrofitClient
import com.devilgamehub.util.NetworkObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class GamesState {
    object Loading : GamesState()
    data class Success(val games: List<Game>, val isOfflineFallback: Boolean = false) : GamesState()
    data class Error(val message: String, val fallbackGames: List<Game> = emptyList()) : GamesState()
}

class GameRepository private constructor(
    context: Context,
    private val networkObserver: NetworkObserver = NetworkObserver.getInstance(context.applicationContext),
    private val settingsRepository: SettingsRepository = SettingsRepository.getInstance(context.applicationContext),
    private val localGameProvider: LocalGameProvider = LocalGameProvider(context.applicationContext),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val userPreferences = settingsRepository.userPreferences
    val isOnline: StateFlow<Boolean> = networkObserver.isOnline

    private val _gamesState = MutableStateFlow<GamesState>(GamesState.Loading)
    val gamesState: StateFlow<GamesState> = _gamesState.asStateFlow()

    private var rawGamesCache: List<Game> = emptyList()

    init {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.userPreferences.collect {
                if (rawGamesCache.isNotEmpty()) {
                    refreshWithLocalPreferences()
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: GameRepository? = null

        fun getInstance(context: Context): GameRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GameRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    suspend fun loadGames(forceRefresh: Boolean = false) {
        withContext(ioDispatcher) {
            _gamesState.value = GamesState.Loading
            val customUrl = settingsRepository.userPreferences.value.customApiUrl
            val localGames = localGameProvider.getDefaultGames()

            if (!isOnline.value) {
                rawGamesCache = localGames
                updateStateWithCombinedData(rawGamesCache, isOfflineFallback = true)
                return@withContext
            }

            try {
                val response = RetrofitClient.apiService.fetchGamesConfig(customUrl)
                if (response.isSuccessful && response.body() != null) {
                    val remoteGames = (response.body()?.games ?: emptyList())
                        .filter { localGameProvider.validateGame(it) }

                    if (remoteGames.isNotEmpty()) {
                        localGameProvider.saveDownloadedGames(remoteGames)
                        rawGamesCache = remoteGames
                        updateStateWithCombinedData(rawGamesCache, isOfflineFallback = false)
                    } else {
                        rawGamesCache = localGames
                        updateStateWithCombinedData(rawGamesCache, isOfflineFallback = true)
                    }
                } else {
                    rawGamesCache = localGames
                    updateStateWithCombinedData(
                        rawGamesCache,
                        isOfflineFallback = true,
                        errorMessage = "Remote JSON HTTP ${response.code()}. Loaded cached games catalog."
                    )
                }
            } catch (_: Exception) {
                rawGamesCache = localGames
                updateStateWithCombinedData(
                    rawGamesCache,
                    isOfflineFallback = true,
                    errorMessage = "Network offline or invalid URL. Loaded cached games catalog."
                )
            }
        }
    }

    private fun updateStateWithCombinedData(
        games: List<Game>,
        isOfflineFallback: Boolean,
        errorMessage: String? = null
    ) {
        val prefs = settingsRepository.userPreferences.value
        val favSet = prefs.favoriteIds
        val recentMap = prefs.recentlyPlayedMap

        val enrichedGames = games.map { game ->
            game.copy(
                isFavorite = favSet.contains(game.id),
                lastPlayedTimestamp = recentMap[game.id] ?: 0L
            )
        }

        if (errorMessage != null && enrichedGames.isEmpty()) {
            _gamesState.value = GamesState.Error(errorMessage, enrichedGames)
        } else {
            _gamesState.value = GamesState.Success(enrichedGames, isOfflineFallback)
        }
    }

    fun refreshWithLocalPreferences() {
        if (rawGamesCache.isNotEmpty()) {
            val isOffline = (_gamesState.value as? GamesState.Success)?.isOfflineFallback ?: false
            updateStateWithCombinedData(rawGamesCache, isOfflineFallback = isOffline)
        }
    }

    fun toggleFavorite(gameId: String) {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.toggleFavorite(gameId)
        }
    }

    fun recordGamePlay(gameId: String) {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.addRecentlyPlayed(gameId)
        }
    }

    fun getGameById(gameId: String): Game? {
        val currentGames = when (val state = _gamesState.value) {
            is GamesState.Success -> state.games
            is GamesState.Error -> state.fallbackGames
            else -> rawGamesCache
        }
        return currentGames.find { it.id == gameId }
    }

    fun setCustomApiUrl(url: String) {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.setCustomApiUrl(url)
        }
    }

    fun resetCustomApiUrl() {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.resetCustomApiUrl()
        }
    }

    fun clearHistory() {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.clearHistory()
        }
    }

    fun deleteSelectedHistory(gameIds: Set<String>) {
        CoroutineScope(ioDispatcher).launch {
            settingsRepository.deleteSelectedHistory(gameIds)
        }
    }
}
