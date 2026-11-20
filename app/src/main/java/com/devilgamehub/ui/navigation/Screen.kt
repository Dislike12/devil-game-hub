package com.devilgamehub.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Library : Screen("library", "Library", Icons.Default.SportsEsports)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Player : Screen("player/{gameId}", "Player") {
        fun createRoute(gameId: String) = "player/$gameId"
    }

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Home, Library, Favorites, Settings)
    }
}
