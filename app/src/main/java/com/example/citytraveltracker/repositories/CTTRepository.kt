package com.example.citytraveltracker.repositories

import com.example.citytraveltracker.model.Destination
import com.example.citytraveltracker.model.Route
import com.example.citytraveltracker.other.Resource
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow

interface CTTRepository {

    suspend fun insertDestination(destination: Destination)

    suspend fun deleteDestination(destination: Destination)

    fun observeAllDestinations(): Flow<List<Destination>>

    suspend fun getDistanceMatrix(origins:List<Route>,destinations:List<Route>): Resource<DistanceMatrixResponse>
}