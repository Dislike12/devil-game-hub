package com.devilgamehub.data.local

import android.content.Context
import android.util.Log
import com.devilgamehub.data.model.Game
import com.devilgamehub.data.model.GameConfig
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

class LocalGameProvider(private val context: Context) {
    private val gson = Gson()
    private val cacheFile: File by lazy {
        File(context.filesDir, "games_cache.json")
    }

    fun getDefaultGames(): List<Game> {
        val cachedGames = getCachedDownloadedGames()
        if (cachedGames.isNotEmpty()) {
            return cachedGames
        }
        val rawGames = loadRawConfig()
        return rawGames.filter { validateGame(it) }
    }

    fun getCachedDownloadedGames(): List<Game> {
        if (!cacheFile.exists()) return emptyList()
        return try {
            FileReader(cacheFile).use { reader ->
                val config = gson.fromJson(reader, GameConfig::class.java)
                val games = config?.games ?: emptyList()
                games.filter { validateGame(it) }
            }
        } catch (e: Exception) {
            Log.w("LocalGameProvider", "Failed to read games_cache.json", e)
            emptyList()
        }
    }

    fun saveDownloadedGames(games: List<Game>) {
        try {
            val validGames = games.filter { validateGame(it) }
            val config = GameConfig(
                version = 1,
                lastUpdated = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                games = validGames
            )
            FileWriter(cacheFile).use { writer ->
                gson.toJson(config, writer)
            }
            Log.d("LocalGameProvider", "Saved ${validGames.size} downloaded games to internal cache.")
        } catch (e: Exception) {
            Log.e("LocalGameProvider", "Failed to save downloaded games to internal cache", e)
        }
    }

    private fun loadRawConfig(): List<Game> {
        val fileNames = listOf("games.json", "default_games.json")
        for (fileName in fileNames) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val config = gson.fromJson(reader, GameConfig::class.java)
                        val games = config?.games ?: emptyList()
                        if (games.isNotEmpty()) {
                            return games
                        }
                    }
                }
            } catch (_: Exception) {
                // Ignore and try next file
            }
        }
        return emptyList()
    }

    fun validateGame(game: Game): Boolean {
        if (game.id.isBlank() || game.title.isBlank()) {
            Log.w("LocalGameProvider", "Skipping invalid game entry (missing ID or title): $game")
            return false
        }

        // Validate local asset path if specified
        if (game.assetPath.isNotBlank() && !game.assetPath.startsWith("http")) {
            val relativePath = game.assetPath.removePrefix("file:///android_asset/").removePrefix("/")
            try {
                context.assets.open(relativePath).close()
            } catch (e: Exception) {
                if (game.gameUrl.isBlank()) {
                    Log.w("LocalGameProvider", "Asset file not found and no remote URL for game '${game.title}': $relativePath")
                    return false
                }
            }
        }

        return true
    }
}
