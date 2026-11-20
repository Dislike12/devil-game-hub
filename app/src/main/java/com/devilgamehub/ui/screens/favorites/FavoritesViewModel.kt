package com.devilgamehub.ui.screens.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devilgamehub.data.model.Game
import com.devilgamehub.data.repository.GameRepository
import com.devilgamehub.data.repository.GamesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    val repository = GameRepository.getInstance(application)

    init {
        if (repository.gamesState.value is GamesState.Loading) {
            viewModelScope.launch {
                repository.loadGames()
            }
        }
    }

    val favoriteGames: StateFlow<List<Game>> = repository.gamesState.map { state ->
        when (state) {
            is GamesState.Success -> state.games.filter { it.isFavorite }
            is GamesState.Error -> state.fallbackGames.filter { it.isFavorite }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyGames: StateFlow<List<Game>> = repository.gamesState.map { state ->
        when (state) {
            is GamesState.Success -> state.games.filter { it.lastPlayedTimestamp > 0 }
                .sortedByDescending { it.lastPlayedTimestamp }
            is GamesState.Error -> state.fallbackGames.filter { it.lastPlayedTimestamp > 0 }
                .sortedByDescending { it.lastPlayedTimestamp }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(game: Game) {
        repository.toggleFavorite(game.id)
    }

    fun recordGamePlay(game: Game) {
        repository.recordGamePlay(game.id)
    }

    fun clearHistory() {
        repository.clearHistory()
    }

    fun deleteSelectedHistory(gameIds: Set<String>) {
        repository.deleteSelectedHistory(gameIds)
    }
}
