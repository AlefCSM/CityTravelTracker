package com.alefmoreira.citytraveltracker.remote

import com.alefmoreira.citytraveltracker.BuildConfig
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceMatrixAPI {

    @GET("/maps/api/distancematrix/json")
    suspend fun getDistanceMatrix(
        @Query("origins", encoded = true) origins: String,
        @Query("destinations", encoded = true) destinations: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY,
        @Query("units") units: String = "metric",
    ): Response<DistanceMatrixResponse>
}