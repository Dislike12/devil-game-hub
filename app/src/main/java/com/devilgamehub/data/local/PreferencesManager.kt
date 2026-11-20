package com.devilgamehub.data.local

import android.content.Context
import android.content.SharedPreferences
import com.devilgamehub.data.model.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("devil_game_hub_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val customUrl = prefs.getString(KEY_REMOTE_URL, UserPreferences.DEFAULT_REMOTE_JSON_URL)
            ?: UserPreferences.DEFAULT_REMOTE_JSON_URL
        val favs = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        val recentJson = prefs.getString(KEY_RECENTLY_PLAYED, "{}") ?: "{}"
        
        val type = object : TypeToken<Map<String, Long>>() {}.type
        val recentMap: Map<String, Long> = try {
            gson.fromJson(recentJson, type) ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }

        return UserPreferences(
            customApiUrl = customUrl,
            isDarkMode = prefs.getBoolean(KEY_DARK_MODE, true),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            bgMusicEnabled = prefs.getBoolean(KEY_BG_MUSIC, true),
            language = prefs.getString(KEY_LANGUAGE, "English") ?: "English",
            autoRotateEnabled = prefs.getBoolean(KEY_AUTO_ROTATE, true),
            hapticFeedbackEnabled = prefs.getBoolean(KEY_HAPTIC, true),
            fastLoadingEnabled = prefs.getBoolean(KEY_FAST_LOADING, true),
            accentTheme = prefs.getString(KEY_ACCENT_THEME, "Neon Cyber") ?: "Neon Cyber",
            fullScreenEnabled = prefs.getBoolean(KEY_FULLSCREEN_ENABLED, true),
            favoriteIds = favs,
            recentlyPlayedMap = recentMap
        )
    }

    fun toggleFavorite(gameId: String) {
        val currentFavs = _userPreferences.value.favoriteIds.toMutableSet()
        if (currentFavs.contains(gameId)) {
            currentFavs.remove(gameId)
        } else {
            currentFavs.add(gameId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, currentFavs).apply()
        _userPreferences.value = _userPreferences.value.copy(favoriteIds = currentFavs)
    }

    fun addRecentlyPlayed(gameId: String) {
        val currentRecent = _userPreferences.value.recentlyPlayedMap.toMutableMap()
        currentRecent[gameId] = System.currentTimeMillis()
        
        val json = gson.toJson(currentRecent)
        prefs.edit().putString(KEY_RECENTLY_PLAYED, json).apply()
        
        _userPreferences.value = _userPreferences.value.copy(recentlyPlayedMap = currentRecent)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(soundEnabled = enabled)
    }

    fun setBgMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BG_MUSIC, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(bgMusicEnabled = enabled)
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _userPreferences.value = _userPreferences.value.copy(language = lang)
    }

    fun setAutoRotateEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_ROTATE, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(autoRotateEnabled = enabled)
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(hapticFeedbackEnabled = enabled)
    }

    fun setFastLoadingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FAST_LOADING, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(fastLoadingEnabled = enabled)
    }

    fun setAccentTheme(theme: String) {
        prefs.edit().putString(KEY_ACCENT_THEME, theme).apply()
        _userPreferences.value = _userPreferences.value.copy(accentTheme = theme)
    }

    fun setCustomApiUrl(url: String) {
        val cleanUrl = url.trim()
        prefs.edit().putString(KEY_REMOTE_URL, cleanUrl).apply()
        _userPreferences.value = _userPreferences.value.copy(customApiUrl = cleanUrl)
    }

    fun resetCustomApiUrl() {
        prefs.edit().remove(KEY_REMOTE_URL).apply()
        _userPreferences.value = _userPreferences.value.copy(customApiUrl = UserPreferences.DEFAULT_REMOTE_JSON_URL)
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_RECENTLY_PLAYED).apply()
        _userPreferences.value = _userPreferences.value.copy(recentlyPlayedMap = emptyMap())
    }

    companion object {
        private const val KEY_REMOTE_URL = "remote_json_url"
        private const val KEY_FAVORITES = "favorite_game_ids"
        private const val KEY_RECENTLY_PLAYED = "recently_played_map"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_BG_MUSIC = "bg_music_enabled"
        private const val KEY_LANGUAGE = "user_language"
        private const val KEY_AUTO_ROTATE = "auto_rotate_enabled"
        private const val KEY_HAPTIC = "haptic_enabled"
        private const val KEY_FAST_LOADING = "fast_loading_enabled"
        private const val KEY_ACCENT_THEME = "accent_theme"
        private const val KEY_FULLSCREEN_ENABLED = "fullscreen_enabled"
    }
}
