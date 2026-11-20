package com.devilgamehub.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devilgamehub.data.local.SettingsRepository
import com.devilgamehub.data.model.UserPreferences
import com.devilgamehub.data.repository.GameRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val repository = GameRepository.getInstance(application)
    val settingsRepo = SettingsRepository.getInstance(application)

    val userPreferences: StateFlow<UserPreferences> = settingsRepo.userPreferences

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setSoundEnabled(enabled)
        }
    }

    fun setBgMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setBgMusicEnabled(enabled)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepo.setLanguage(lang)
        }
    }

    fun setAutoRotateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setAutoRotateEnabled(enabled)
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setHapticFeedbackEnabled(enabled)
        }
    }

    fun setFastLoadingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setFastLoadingEnabled(enabled)
        }
    }

    fun setAccentTheme(theme: String) {
        viewModelScope.launch {
            settingsRepo.setAccentTheme(theme)
        }
    }

    fun setCustomApiUrl(url: String) {
        viewModelScope.launch {
            settingsRepo.setCustomApiUrl(url)
            repository.loadGames(forceRefresh = true)
        }
    }

    fun resetCustomApiUrl() {
        viewModelScope.launch {
            settingsRepo.resetCustomApiUrl()
            repository.loadGames(forceRefresh = true)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            settingsRepo.clearHistory()
            repository.clearHistory()
        }
    }
}
