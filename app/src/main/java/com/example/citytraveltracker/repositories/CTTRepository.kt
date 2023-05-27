package com.example.citytraveltracker.repositories

import com.example.citytraveltracker.model.Route
import com.example.citytraveltracker.other.Resource
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow

interface CTTRepository {

    suspend fun insertRoute(route: Route)

    suspend fun deleteRoute(route: Route)

    fun observeAllRoutes(): Flow<List<Route>>

    suspend fun getDistanceMatrix(origins:List<Route>,destinations:List<Route>): Resource<DistanceMatrixResponse>
}