package com.devilgamehub.data.model

import com.google.gson.annotations.SerializedName

data class Game(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("category")
    val category: String = "Arcade",
    
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String = "",
    
    @SerializedName("gameUrl")
    val gameUrl: String = "",
    
    @SerializedName("assetPath")
    val assetPath: String = "",
    
    @SerializedName("type")
    val type: String = "html5", // html5, 2d, 3d, url
    
    @SerializedName("isFeatured")
    val isFeatured: Boolean = false,
    
    @SerializedName("isPopular")
    val isPopular: Boolean = false,
    
    @SerializedName("rating")
    val rating: Float = 4.5f,
    
    @SerializedName("playsCount")
    val playsCount: Int = 1000,
    
    @SerializedName("orientation")
    val orientation: String = "portrait", // portrait, landscape, any
    
    @SerializedName("version")
    val version: Int = 1,
    
    @SerializedName("tags")
    val tags: List<String> = emptyList(),
    
    val isFavorite: Boolean = false,
    val lastPlayedTimestamp: Long = 0L
) {
    val resolvedGameUrl: String
        get() {
            return when {
                assetPath.isNotBlank() && !assetPath.startsWith("http") && !assetPath.startsWith("file://") -> {
                    "file:///android_asset/${assetPath.removePrefix("/")}"
                }
                gameUrl.isNotBlank() -> gameUrl
                else -> ""
            }
        }
}
