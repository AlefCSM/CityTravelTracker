package com.example.citytraveltracker.repositories

import com.example.citytraveltracker.data.dao.CityDAO
import com.example.citytraveltracker.model.Route
import com.example.citytraveltracker.other.Resource
import com.example.citytraveltracker.remote.DistanceMatrixAPI
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
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

    override fun observeAllRoutes(): Flow<List<Route>> {
        return flow {
            val routeList = mutableListOf<Route>()
            cityDAO.observeAllCities().collectLatest { citiesList ->
                citiesList.forEach { city ->
                    cityDAO.observeCityConnectionsByCityId(city.id!!)
                        .collectLatest { connectionsList ->
                            Route(city, connectionsList.toMutableList())
                            routeList.add(Route(city, connectionsList.toMutableList()))
                            emit(routeList)
                        }
                }
            }
        }
    }


    override suspend fun getDistanceMatrix(
        origins: List<Route>,
        destinations: List<Route>
    ): Resource<DistanceMatrixResponse> {
        return try {

            val separator = "|"
            val originsString = origins.joinToString(separator){ "placeId:${it.city.placeId}"}
            val destinationsString = destinations.joinToString(separator){ "placeId:${it.city.placeId}"}

            val response = distanceMatrixAPI.getDistanceMatrix(originsString, destinationsString)

            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Unknown error", null)
            } else {
                Resource.error("Unknown error", null)
            }
        } catch (e: Exception) {
            Resource.error("Could not reach server", null)
        }
    }
}