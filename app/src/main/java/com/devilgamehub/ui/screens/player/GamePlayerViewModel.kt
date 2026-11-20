package com.devilgamehub.ui.screens.player

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devilgamehub.data.local.SettingsRepository
import com.devilgamehub.data.model.Game
import com.devilgamehub.data.model.UserPreferences
import com.devilgamehub.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GamePlayerViewModel(application: Application) : AndroidViewModel(application) {
    val repository = GameRepository.getInstance(application)
    val settingsRepo = SettingsRepository.getInstance(application)

    val userPreferences: StateFlow<UserPreferences> = settingsRepo.userPreferences

    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()

    private val _loadProgress = MutableStateFlow(0)
    val loadProgress: StateFlow<Int> = _loadProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _requestedOrientation = MutableStateFlow(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    val requestedOrientation: StateFlow<Int> = _requestedOrientation.asStateFlow()

    init {
        viewModelScope.launch {
            repository.gamesState.collect {
                val gameId = _currentGame.value?.id
                if (gameId != null) {
                    val updatedGame = repository.getGameById(gameId)
                    if (updatedGame != null) {
                        _currentGame.value = updatedGame
                    }
                }
            }
        }
    }

    fun loadGame(gameId: String) {
        val game = repository.getGameById(gameId)
        _currentGame.value = game
        if (game != null) {
            repository.recordGamePlay(game.id)

            val autoRotate = userPreferences.value.autoRotateEnabled
            if (autoRotate) {
                when (game.orientation.lowercase()) {
                    "landscape" -> _requestedOrientation.value = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    "portrait" -> _requestedOrientation.value = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    else -> _requestedOrientation.value = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            } else {
                _requestedOrientation.value = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        } else {
            setError("Game not found in database.")
        }
    }

    fun toggleOrientation() {
        _requestedOrientation.value = if (_requestedOrientation.value == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    fun toggleGameMute(gameId: String) {
        viewModelScope.launch {
            settingsRepo.toggleGameMute(gameId)
        }
    }

    fun updateProgress(progress: Int) {
        _loadProgress.value = progress
        if (progress >= 100) {
            _isLoading.value = false
        }
    }

    fun setError(error: String) {
        _hasError.value = true
        _errorMessage.value = error
        _isLoading.value = false
    }

    fun clearError() {
        _hasError.value = false
        _errorMessage.value = ""
        _isLoading.value = true
        _loadProgress.value = 0
    }
}
