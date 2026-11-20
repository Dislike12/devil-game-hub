package com.devilgamehub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.devilgamehub.data.model.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository private constructor(private val context: Context) {

    private val dataStore = context.dataStore
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    val userPreferences: StateFlow<UserPreferences> = dataStore.data
        .map { prefs ->
            val customUrl = prefs[KEY_REMOTE_URL] ?: UserPreferences.DEFAULT_REMOTE_JSON_URL
            val favs = prefs[KEY_FAVORITES] ?: emptySet()
            val mutedGames = prefs[KEY_MUTED_GAMES] ?: emptySet()
            val recentJson = prefs[KEY_RECENTLY_PLAYED] ?: "{}"

            val type = object : TypeToken<Map<String, Long>>() {}.type
            val recentMap: Map<String, Long> = try {
                gson.fromJson(recentJson, type) ?: emptyMap()
            } catch (_: Exception) {
                emptyMap()
            }

            UserPreferences(
                customApiUrl = customUrl,
                isDarkMode = prefs[KEY_DARK_MODE] ?: true,
                soundEnabled = prefs[KEY_SOUND_ENABLED] ?: true,
                bgMusicEnabled = prefs[KEY_BG_MUSIC] ?: true,
                language = prefs[KEY_LANGUAGE] ?: "English",
                autoRotateEnabled = prefs[KEY_AUTO_ROTATE] ?: true,
                hapticFeedbackEnabled = prefs[KEY_HAPTIC] ?: true,
                fastLoadingEnabled = prefs[KEY_FAST_LOADING] ?: true,
                accentTheme = prefs[KEY_ACCENT_THEME] ?: "Neon Cyber",
                fullScreenEnabled = prefs[KEY_FULLSCREEN] ?: true,
                favoriteIds = favs,
                mutedGameIds = mutedGames,
                recentlyPlayedMap = recentMap
            )
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = UserPreferences()
        )

    suspend fun toggleFavorite(gameId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITES] ?: emptySet()
            val updated = if (current.contains(gameId)) current - gameId else current + gameId
            prefs[KEY_FAVORITES] = updated
        }
    }

    suspend fun toggleGameMute(gameId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_MUTED_GAMES] ?: emptySet()
            val updated = if (current.contains(gameId)) current - gameId else current + gameId
            prefs[KEY_MUTED_GAMES] = updated
        }
    }

    suspend fun addRecentlyPlayed(gameId: String) {
        dataStore.edit { prefs ->
            val recentJson = prefs[KEY_RECENTLY_PLAYED] ?: "{}"
            val type = object : TypeToken<MutableMap<String, Long>>() {}.type
            val currentMap: MutableMap<String, Long> = try {
                gson.fromJson(recentJson, type) ?: mutableMapOf()
            } catch (_: Exception) {
                mutableMapOf()
            }
            currentMap[gameId] = System.currentTimeMillis()
            prefs[KEY_RECENTLY_PLAYED] = gson.toJson(currentMap)
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setBgMusicEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_BG_MUSIC] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    suspend fun setAutoRotateEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_ROTATE] = enabled }
    }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_HAPTIC] = enabled }
    }

    suspend fun setFastLoadingEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_FAST_LOADING] = enabled }
    }

    suspend fun setAccentTheme(theme: String) {
        dataStore.edit { it[KEY_ACCENT_THEME] = theme }
    }

    suspend fun setCustomApiUrl(url: String) {
        dataStore.edit { it[KEY_REMOTE_URL] = url.trim() }
    }

    suspend fun resetCustomApiUrl() {
        dataStore.edit { it.remove(KEY_REMOTE_URL) }
    }

    suspend fun clearHistory() {
        dataStore.edit { it.remove(KEY_RECENTLY_PLAYED) }
    }

    suspend fun deleteSelectedHistory(gameIds: Set<String>) {
        dataStore.edit { prefs ->
            val recentJson = prefs[KEY_RECENTLY_PLAYED] ?: "{}"
            val type = object : TypeToken<MutableMap<String, Long>>() {}.type
            val currentMap: MutableMap<String, Long> = try {
                gson.fromJson(recentJson, type) ?: mutableMapOf()
            } catch (_: Exception) {
                mutableMapOf()
            }
            gameIds.forEach { currentMap.remove(it) }
            if (currentMap.isEmpty()) {
                prefs.remove(KEY_RECENTLY_PLAYED)
            } else {
                prefs[KEY_RECENTLY_PLAYED] = gson.toJson(currentMap)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }

        private val KEY_REMOTE_URL = stringPreferencesKey("remote_json_url")
        private val KEY_FAVORITES = stringSetPreferencesKey("favorite_game_ids")
        private val KEY_MUTED_GAMES = stringSetPreferencesKey("muted_game_ids")
        private val KEY_RECENTLY_PLAYED = stringPreferencesKey("recently_played_map")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val KEY_BG_MUSIC = booleanPreferencesKey("bg_music_enabled")
        private val KEY_LANGUAGE = stringPreferencesKey("user_language")
        private val KEY_AUTO_ROTATE = booleanPreferencesKey("auto_rotate_enabled")
        private val KEY_HAPTIC = booleanPreferencesKey("haptic_enabled")
        private val KEY_FAST_LOADING = booleanPreferencesKey("fast_loading_enabled")
        private val KEY_ACCENT_THEME = stringPreferencesKey("accent_theme")
        private val KEY_FULLSCREEN = booleanPreferencesKey("fullscreen_enabled")
    }
}
