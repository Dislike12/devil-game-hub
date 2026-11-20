package com.devilgamehub.ui.screens.home

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

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val repository = GameRepository.getInstance(application)

    val gamesState: StateFlow<GamesState> = repository.gamesState
    val isOnline: StateFlow<Boolean> = repository.isOnline

    val featuredGames: StateFlow<List<Game>> = gamesState.map { state ->
        when (state) {
            is GamesState.Success -> state.games.filter { it.isFeatured }
            is GamesState.Error -> state.fallbackGames.filter { it.isFeatured }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularGames: StateFlow<List<Game>> = gamesState.map { state ->
        when (state) {
            is GamesState.Success -> state.games.filter { it.isPopular }
            is GamesState.Error -> state.fallbackGames.filter { it.isPopular }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayedGames: StateFlow<List<Game>> = gamesState.map { state ->
        when (state) {
            is GamesState.Success -> state.games.filter { it.lastPlayedTimestamp > 0 }
                .sortedByDescending { it.lastPlayedTimestamp }
            is GamesState.Error -> state.fallbackGames.filter { it.lastPlayedTimestamp > 0 }
                .sortedByDescending { it.lastPlayedTimestamp }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshGames()
    }

    fun refreshGames() {
        viewModelScope.launch {
            repository.loadGames()
        }
    }

    fun toggleFavorite(game: Game) {
        repository.toggleFavorite(game.id)
    }

    fun recordGamePlay(game: Game) {
        repository.recordGamePlay(game.id)
    }
}
