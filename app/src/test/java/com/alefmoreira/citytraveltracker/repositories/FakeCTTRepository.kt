package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.model.Dashboard
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Resource


class FakeCTTRepository : CTTRepository {

    private val routes = mutableListOf<Route>()
    private var shouldReturnNetworkError = false
    private var idCounter = 1L

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }


    override suspend fun insertRoute(route: Route) {
        route.city.id = idCounter++
        routes.add(route)
    }

    override suspend fun deleteRoute(route: Route) {
        routes.remove(route)
    }

    override suspend fun getRouteById(id: Long): Route {
        return routes.first { it.city.id == id }
    }

    override suspend fun getAllRoutes(): List<Route> {
        return routes
    }

    override suspend fun getDashboard(routes: List<Route>): Resource<Dashboard> {
        return Resource.loading()
    }
}