package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class FakeCTTRepository : CTTRepository {

    private val routes = mutableListOf<Route>()
    private val observableDestinations = MutableStateFlow<List<Route>>(routes)
    private var shouldReturnNetworkError = false
    private var idCounter = 1L

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshFlows() {
        observableDestinations.value = routes
    }

    override suspend fun insertRoute(route: Route) {
        route.city.id = idCounter++
        routes.add(route)
        refreshFlows()
    }

    override suspend fun deleteRoute(route: Route) {
        routes.remove(route)
        refreshFlows()
    }

    override suspend fun getRouteById(id: Long): Route {
        return routes.first { it.city.id == id }
    }

    override suspend fun getAllRoutes(): Flow<List<Route>> {
        return observableDestinations
    }

    override suspend fun getDistanceMatrix(routes: List<Route>): Resource<DistanceMatrixResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(DistanceMatrixResponse(listOf(), listOf(), listOf(), ""))
        }
    }
}