package com.devilgamehub.data.remote

import com.devilgamehub.data.model.GameConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GitHubApiService {
    @GET
    suspend fun fetchGamesConfig(@Url fullUrl: String): Response<GameConfig>
}
