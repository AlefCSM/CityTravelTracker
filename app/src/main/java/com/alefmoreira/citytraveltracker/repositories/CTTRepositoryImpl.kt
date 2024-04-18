package com.alefmoreira.citytraveltracker.repositories

import android.content.SharedPreferences
import com.alefmoreira.citytraveltracker.data.dao.CityDAO
import com.alefmoreira.citytraveltracker.model.Dashboard
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Constants
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.remote.DistanceMatrixAPI
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.ceil

class CTTRepositoryImpl @Inject constructor(
    private val cityDAO: CityDAO,
    private val distanceMatrixAPI: DistanceMatrixAPI,
    private val sharedPreferences: SharedPreferences
) : CTTRepository {

    private val prefs = sharedPreferences.edit()

    private var _mileage = MutableStateFlow((Constants.INITIAL_LONG).toString())
    val mileage: StateFlow<String> = _mileage

    private var _time = MutableStateFlow((Constants.INITIAL_TIME))
    val time: StateFlow<String> = _time
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

    override suspend fun getDistanceMatrix(routes: List<Route>): Resource<DistanceMatrixResponse> {
        return try {
            val originsString = routeFormatToStringArgument(routes.toMutableList(), true)
            val destinationsString = routeFormatToStringArgument(routes.toMutableList(), false)

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

    override suspend fun getDashboard(routes: List<Route>): Resource<Dashboard> {
        return try {
            val originsString = routeFormatToStringArgument(routes.toMutableList(), true)
            val destinationsString = routeFormatToStringArgument(routes.toMutableList(), false)

            distanceMatrixAPI.getDistanceMatrix(originsString, destinationsString).run {
                val response = this
                if (response.isSuccessful) {
                    response.body()?.let {

                        try {
                            Resource.success(distanceMatrixToDashboard(it))
                        }catch (e:Exception){
                            Resource.error("",null)
                        }

                    } ?: Resource.error("Unknown error", null)
                } else {
                    Resource.error("Unknown error", null)
                }
            }
        } catch (e: Exception) {
            Resource.error("Could not reach server", null)
        }
    }

    private fun distanceMatrixToDashboard(distanceMatrix:DistanceMatrixResponse):Dashboard{
        val dashboard = Dashboard("",0L)


        return dashboard
    }

    private fun routeFormatToStringArgument(routes: MutableList<Route>, isOrigin: Boolean): String {
        if (isOrigin) {
            routes.removeLast()
        } else {
            routes.removeFirst()
        }

        val placeIdList: MutableList<String> = mutableListOf()

        routes.forEach { route ->
            route.connections.forEach { connection ->
                placeIdList.add(connection.placeId)
            }
            placeIdList.add(route.city.placeId)
        }

        val separator = "|"
        return placeIdList.joinToString(separator) { "place_id:$it" }
    }

    private fun handleMatrix(matrixResponse: DistanceMatrixResponse) {
        var seconds: Long = Constants.INITIAL_LONG
        var meters: Long = Constants.INITIAL_LONG

        if (matrixResponse.status == "REQUEST_DENIED") {
            return
        }

        try {
            matrixResponse.rows.forEachIndexed { index, distanceMatrixRow ->
                seconds += distanceMatrixRow.elements[index].duration.value
                meters += distanceMatrixRow.elements[index].distance.value
            }
        } catch (e: Exception) {
//            _distanceMatrixStatus.value = Event(Resource.error(Constants.CALCULUS_ERROR, null))
            _mileage.value = Constants.INITIAL_LONG.toString()
            _time.value = Constants.INITIAL_TIME
            return
        }

        setMileage(meters)
        setHours(seconds)

        prefs.putString(Constants.ROUTE_LIST, routesToJson())
        prefs.putString(Constants.MATRIX_MILEAGE, mileage.value.replace(" km", ""))
        prefs.putString(Constants.MATRIX_TIME, time.value)
        prefs.commit()
    }

    private fun setMileage(meters: Long) {
        _mileage.value = if (meters == 0L) {
            "0"
        } else {
            "${ceil(meters.toDouble() / Constants.METERS_IN_KM).toLong()}"
        }
    }

    private fun setHours(seconds: Long) {
        val horas = seconds / Constants.SECONDS_IN_HOUR
        val diff = if (horas > 0) {
            seconds % (horas * Constants.SECONDS_IN_HOUR)
        } else {
            seconds % Constants.SECONDS_IN_HOUR
        }
        val minutes = ceil(diff.toDouble() / Constants.SECONDS_IN_MINUTE).toLong()

        _time.value = "${horas}h $minutes min"
    }

    private fun routesToJson(): String {
        val jsonArray = JSONArray()

        routes.value.forEach {
            val jsonObject = JSONObject()

            jsonObject.put("city", it.city)
            jsonObject.put("connections", it.connections)

            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    private fun getStoredPrefs() {
        _mileage.value = sharedPreferences.getString(Constants.MATRIX_MILEAGE, "0").toString()
        _time.value = sharedPreferences.getString(Constants.MATRIX_TIME, Constants.INITIAL_TIME)!!
    }

    private fun resetDashboard() {
        _mileage.value = Constants.INITIAL_LONG.toString()
        _time.value = Constants.INITIAL_TIME
    }
}