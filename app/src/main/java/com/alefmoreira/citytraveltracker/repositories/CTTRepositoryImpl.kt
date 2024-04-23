package com.alefmoreira.citytraveltracker.repositories

import android.content.SharedPreferences
import android.os.Bundle
import com.alefmoreira.citytraveltracker.data.dao.CityDAO
import com.alefmoreira.citytraveltracker.model.Dashboard
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Constants
import com.alefmoreira.citytraveltracker.other.Constants.ROUTE_LIST
import com.alefmoreira.citytraveltracker.other.Constants.STRING_SEPARATOR
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.remote.DistanceMatrixAPI
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class CTTRepositoryImpl @Inject constructor(
    private val cityDAO: CityDAO,
    private val distanceMatrixAPI: DistanceMatrixAPI,
    private val sharedPreferences: SharedPreferences,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
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
        val connections = city.id?.let { cityDAO.getCityConnectionsByCityId(it) } ?: emptyList()

        return Route(city, connections.toMutableList())
    }

    override suspend fun getAllRoutes(): List<Route> {
        val routeList = mutableListOf<Route>()

        cityDAO.getAllCities().forEach { city ->
            city.id?.let {
                val connections = cityDAO.getCityConnectionsByCityId(it)
                routeList.add(Route(city, connections.toMutableList()))
            }
        }
        return routeList
    }

    override suspend fun getDashboard(routes: List<Route>): Resource<Dashboard> {
        return if (shouldUpdate(routes)) {
            getDashboardFromAPI(routes)
        } else {
            getDashboardFromCache()
        }
    }

    private fun shouldUpdate(routes: List<Route>): Boolean {
        val savedListString = sharedPreferences.getString(ROUTE_LIST, "")
        val jsonRoutes = routesToJson(routes)
        return savedListString != jsonRoutes
    }

    private suspend fun getDashboardFromAPI(routes: List<Route>): Resource<Dashboard> {
        return try {
            val originsString = routeFormatToStringArgument(routes.toMutableList(), true)
            val destinationsString = routeFormatToStringArgument(routes.toMutableList(), false)

            distanceMatrixAPI.getDistanceMatrix(originsString, destinationsString).run {
                val response = this
                if (response.isSuccessful) {
                    response.body()?.let {
                        saveRoutesToPrefs(routes)
                        Resource.success(distanceMatrixToDashboard(it))
                    } ?: throw Exception("Empty response error")
                } else {
                    throw Exception("Response error")
                }
            }
        } catch (e: Exception) {
            e.message?.let {
                firebaseCrashlytics.log(it)
            }
            println("*******")
            print(e.message)
            val bundle = Bundle().apply {
                this.putString("exception_message", e.message)
            }
            firebaseAnalytics.logEvent("getDashboardFromAPI", bundle)
            Resource.error("${e.message}", null)
        }
    }

    private fun routeFormatToStringArgument(routes: MutableList<Route>, isOrigin: Boolean): String {
        if (isOrigin) {
            routes.removeLast()
        } else {
            routes.removeFirst()
        }

        val placeIdList = routesToStringList(routes)

        return placeIdList.joinToString(STRING_SEPARATOR) { "place_id:$it" }
    }

    private fun routesToStringList(routes: MutableList<Route>): MutableList<String> {
        val placeIdList: MutableList<String> = mutableListOf()

        routes.forEach { route ->
            route.connections.forEach { connection ->
                placeIdList.add(connection.placeId)
            }
            placeIdList.add(route.city.placeId)
        }
        return placeIdList
    }

    private fun saveRoutesToPrefs(routeList: List<Route>) {
        val prefs = sharedPreferences.edit()
        prefs.putString(ROUTE_LIST, routesToJson(routeList))
        prefs.apply()
    }

    private fun distanceMatrixToDashboard(distanceMatrix: DistanceMatrixResponse): Dashboard {
        if (distanceMatrix.status == "REQUEST_DENIED") {
            throw Exception("Request denied!")
        }
        val dashboard = Dashboard(sharedPreferences)

        try {
            dashboard.calculateDistanceMatrix(distanceMatrix.rows)
            dashboard.saveToPrefs()
        } catch (e: Exception) {
            val bundle = Bundle().apply {
                this.putString("exception_message", e.message)
            }
            firebaseAnalytics.logEvent("distanceMatrixToDashboard", bundle)
            throw Exception(Constants.CALCULUS_ERROR + ": ${e.message}")
        }

        return dashboard
    }

    private fun getDashboardFromCache(): Resource<Dashboard> {
        lateinit var dashboard: Dashboard
        return try {
            dashboard = Dashboard(sharedPreferences).getDashboardFromPrefs()
            Resource.success(dashboard)
        } catch (e: Exception) {
            val bundle = Bundle().apply {
                this.putString("exception_message", e.message)
            }
            e.message?.let {
                firebaseCrashlytics.log(it)
            }

            firebaseAnalytics.logEvent("getDashboardFromCache", bundle)
            Resource.error("Error fetching cache: ${e.message}")
        }
    }

    private fun routesToJson(routes: List<Route>): String {
        val jsonArray = JSONArray()

        routes.forEach {
            val jsonObject = JSONObject()

            jsonObject.put("city", it.city)
            jsonObject.put("connections", it.connections)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
}