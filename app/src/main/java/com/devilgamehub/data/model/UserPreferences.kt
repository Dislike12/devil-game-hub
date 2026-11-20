package com.devilgamehub.data.model

data class UserPreferences(
    val customApiUrl: String = DEFAULT_REMOTE_JSON_URL,
    val isDarkMode: Boolean = true,
    val soundEnabled: Boolean = true,
    val bgMusicEnabled: Boolean = true,
    val language: String = "English",
    val autoRotateEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val fastLoadingEnabled: Boolean = true,
    val accentTheme: String = "Neon Cyber",
    val fullScreenEnabled: Boolean = true,
    val favoriteIds: Set<String> = emptySet(),
    val mutedGameIds: Set<String> = emptySet(),
    val recentlyPlayedMap: Map<String, Long> = emptyMap() // gameId -> timestamp
) {
    fun isGameAudioMuted(gameId: String): Boolean {
        // Global master music OFF overrides individual game setting
        if (!bgMusicEnabled) return true
        return mutedGameIds.contains(gameId)
    }

    companion object {
        const val DEFAULT_REMOTE_JSON_URL = "https://raw.githubusercontent.com/devilgamehub/games-db/main/games.json"
        val LANGUAGES = listOf("English", "Español", "Français", "Deutsch", "日本語", "Português", "हिन्दी")
        val ACCENT_THEMES = listOf("Neon Cyber", "Cyber Pink", "Matrix Green")
    }
}
