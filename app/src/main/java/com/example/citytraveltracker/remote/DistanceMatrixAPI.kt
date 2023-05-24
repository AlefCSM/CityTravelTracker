package com.example.citytraveltracker.remote

import com.example.citytraveltracker.BuildConfig
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceMatrixAPI {

    @GET("/maps/api/distancematrix/")
    suspend fun getDistanceMatrix(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): Response<DistanceMatrixResponse>
}