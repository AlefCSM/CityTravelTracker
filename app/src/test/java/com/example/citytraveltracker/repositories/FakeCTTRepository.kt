package com.example.citytraveltracker.repositories

import com.example.citytraveltracker.model.Route
import com.example.citytraveltracker.other.Resource
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class FakeCTTRepository : CTTRepository {

    private val routes = mutableListOf<Route>()
    private val observableDestinations = MutableStateFlow<List<Route>>(routes)
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshFlows() {
        observableDestinations.value = routes
    }

    override suspend fun insertRoute(route: Route) {
        routes.add(route)
        refreshFlows()
    }

    override suspend fun deleteRoute(route: Route) {
        routes.remove(route)
        refreshFlows()
    }

    override fun observeAllRoutes(): Flow<List<Route>> {
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