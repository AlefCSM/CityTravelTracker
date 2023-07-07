package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow

interface CTTRepository {

    suspend fun insertRoute(route: Route)

    suspend fun deleteRoute(route: Route)

    suspend fun getRouteById(id: Long): Route

    suspend fun getAllRoutes(): Flow<List<Route>>

    suspend fun getDistanceMatrix(
        origins: List<Route>,
        destinations: List<Route>
    ): Resource<DistanceMatrixResponse>
}