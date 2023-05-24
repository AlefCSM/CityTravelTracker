package com.example.citytraveltracker.repositories

import com.example.citytraveltracker.model.Destination
import com.example.citytraveltracker.model.Route
import com.example.citytraveltracker.other.Resource
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class FakeCTTRepository : CTTRepository {

    private val destinations = mutableListOf<Destination>()
    private val observableDestinations = MutableStateFlow<List<Destination>>(destinations)
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshFlows() {
        observableDestinations.value = destinations
    }

    override suspend fun insertDestination(destination: Destination) {
        destinations.add(destination)
        refreshFlows()
    }

    override suspend fun deleteDestination(destination: Destination) {
        destinations.remove(destination)
        refreshFlows()
    }

    override fun observeAllDestinations(): Flow<List<Destination>> {
        return observableDestinations
    }


    override suspend fun getDistanceMatrix(
        origins: List<Route>,
        destinations: List<Route>
    ): Resource<DistanceMatrixResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(DistanceMatrixResponse(listOf(), listOf(), listOf(), ""))
        }
    }
}