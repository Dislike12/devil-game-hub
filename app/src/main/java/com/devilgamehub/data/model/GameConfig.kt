package com.devilgamehub.data.model

import com.google.gson.annotations.SerializedName

data class GameConfig(
    @SerializedName("version")
    val version: Int = 1,
    
    @SerializedName("lastUpdated")
    val lastUpdated: String = "",
    
    @SerializedName("games")
    val games: List<Game> = emptyList()
)
