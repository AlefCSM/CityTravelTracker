package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.data.dao.CityDAO
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.remote.DistanceMatrixAPI
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class DefaultCTTRepository @Inject constructor(
    private val cityDAO: CityDAO,
    private val distanceMatrixAPI: DistanceMatrixAPI
) : CTTRepository {
    override suspend fun insertRoute(route: Route) {
        val cityId = cityDAO.insertCity(route.city)
        val connectionsList = route.connections

        if (connectionsList.isNotEmpty()) {
            connectionsList.forEach { it.cityId = cityId }
            cityDAO.insertConnectionsList(connectionsList)
        }
    }

    override suspend fun deleteRoute(route: Route) {
        cityDAO.deleteCity(route.city)
    }

    override suspend fun getRouteById(id: Long): Route {
        val city = cityDAO.getCityById(id)
        val connections = cityDAO.getCityConnectionsByCityId(city.id!!)

        return Route(city, connections.toMutableList())
    }

    override suspend fun getAllRoutes(): Flow<List<Route>> {
        return channelFlow {
            val routeList = mutableListOf<Route>()
            val cities = cityDAO.getAllCities()

            cities.forEach { city ->
                val connections = cityDAO.getCityConnectionsByCityId(city.id!!)

                routeList.add(Route(city, connections.toMutableList()))
            }

            send(routeList)
        }
    }


    override suspend fun getDistanceMatrix(
        routes: List<Route>
    ): Resource<DistanceMatrixResponse> {
        return try {

            val origins: MutableList<String> = mutableListOf()
            val destinations: MutableList<String> = mutableListOf()


            routes.forEach { route ->
                route.connections.forEach { connection ->
                    origins.add(connection.placeId)
                }

                if (route !== routes.last()) {
                    origins.add(route.city.placeId)
                }
            }
            routes.forEach { route ->
                route.connections.forEach { connection ->
                    destinations.add(connection.placeId)
                }

                if (route !== routes.first()) {
                    destinations.add(route.city.placeId)
                }
            }


            val separator = "|"
            val originsString = origins.joinToString(separator) { "place_id:$it" }
            val destinationsString =
                destinations.joinToString(separator) { "place_id:$it" }

            distanceMatrixAPI.getDistanceMatrix(originsString, destinationsString).run {

                val response = this
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.success(it)
                    } ?: Resource.error("Unknown error", null)
                } else {
                    Resource.error("Unknown error", null)
                }
            }


        } catch (e: Exception) {
            Resource.error("Could not reach server", null)
        }
    }
}