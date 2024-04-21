package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.model.Dashboard
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Resource

interface CTTRepository {

    suspend fun insertRoute(route: Route)

    suspend fun deleteRoute(route: Route)

    suspend fun getRouteById(id: Long): Route

    suspend fun getAllRoutes(): List<Route>

    suspend fun getDashboard(routes: List<Route>): Resource<Dashboard>
}