package com.devilgamehub.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val iconName: String = "sports_esports",
    val colorHex: String = "#A855F7"
)

object Categories {
    val ALL = Category("all", "All Games", "grid_view", "#A855F7")
    val ACTION = Category("Action", "Action", "local_fire_department", "#FF0055")
    val ARCADE = Category("Arcade", "Arcade", "sports_esports", "#00F0FF")
    val PUZZLE = Category("Puzzle", "Puzzle", "extension", "#C084FC")
    val RACING = Category("Racing", "Racing", "directions_car", "#FF9900")
    val RETRO = Category("Retro", "Retro", "videogame_asset", "#00FF66")
    val SPORTS = Category("Sports", "Sports", "sports_soccer", "#38BDF8")
    val STRATEGY = Category("Strategy", "Strategy", "psychology", "#EC4899")

    val list = listOf(
        ALL, ACTION, ARCADE, PUZZLE, RACING, RETRO, SPORTS, STRATEGY
    )
}
