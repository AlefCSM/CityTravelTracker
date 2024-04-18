package com.alefmoreira.citytraveltracker.views.fragments.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.other.Constants.CALCULUS_ERROR
import com.alefmoreira.citytraveltracker.other.Constants.FEW_ELEMENTS_ERROR
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_LONG
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_TIME
import com.alefmoreira.citytraveltracker.other.Constants.MATRIX_MILEAGE
import com.alefmoreira.citytraveltracker.other.Constants.MATRIX_TIME
import com.alefmoreira.citytraveltracker.other.Constants.METERS_IN_KM
import com.alefmoreira.citytraveltracker.other.Constants.ROUTE_LIST
import com.alefmoreira.citytraveltracker.other.Constants.SECONDS_IN_HOUR
import com.alefmoreira.citytraveltracker.other.Constants.SECONDS_IN_MINUTE
import com.alefmoreira.citytraveltracker.other.Constants.TWO_ELEMENTS
import com.alefmoreira.citytraveltracker.other.Event
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider,
    private val networkObserver: NetworkObserver,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val prefs = sharedPreferences.edit()

    private var _mileage = MutableStateFlow((INITIAL_LONG).toString())
    val mileage: StateFlow<String> = _mileage

    private var _time = MutableStateFlow((INITIAL_TIME))
    val time: StateFlow<String> = _time

    private var _networkStatus = MutableStateFlow(NetworkObserver.NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkObserver.NetworkStatus> = _networkStatus

    private var _routes = MutableStateFlow<List<Route>>(mutableListOf())

    private val routes: StateFlow<List<Route>> = _routes

    private var _recyclerList = MutableStateFlow<Resource<List<Route>>>(Resource.init())

    val recyclerList: SharedFlow<Resource<List<Route>>> = _recyclerList

    private var _routeStatus = MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))
    val routeStatus: StateFlow<Event<Resource<Route>>> = _routeStatus

    private var _distanceMatrixStatus = MutableStateFlow<Event<Resource<DistanceMatrixResponse>>>(
        Event(
            Resource.init()
        )
    )

    var distanceMatrixStatus: MutableStateFlow<Event<Resource<DistanceMatrixResponse>>> =
        _distanceMatrixStatus

    private var _originStatus = MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val originStatus: StateFlow<Event<Resource<Route>>> = _originStatus

    private var _destinationStatus =
        MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val destinationStatus: StateFlow<Event<Resource<Route>>> = _destinationStatus


    init {
        checkNetwork()
        observeNetwork()
        observeDistanceMatrix()
    }

    private fun checkNetwork() {
        if (networkObserver.isConnected()) {
            _networkStatus.value = NetworkObserver.NetworkStatus.Available
        } else {
            _networkStatus.value = NetworkObserver.NetworkStatus.Unavailable
        }
    }

    private fun observeNetwork() = viewModelScope.launch(dispatcher.io) {
        networkObserver.observe().collectLatest {
            _networkStatus.value = it
        }
    }

    private fun observeDistanceMatrix() = viewModelScope.launch(dispatcher.io) {
        distanceMatrixStatus.collectLatest {
            val response = it.getContentIfNotHandled()

            response?.let {
                if (response.status == Status.SUCCESS) {
                    response.data?.let { matrix ->
                        handleMatrix(matrix)
                    }
                }
            }
        }
    }

    private fun handleMatrix(matrixResponse: DistanceMatrixResponse) {
        var seconds: Long = INITIAL_LONG
        var meters: Long = INITIAL_LONG

        if (matrixResponse.status == "REQUEST_DENIED") {
            return
        }

        try {
            matrixResponse.rows.forEachIndexed { index, distanceMatrixRow ->
                seconds += distanceMatrixRow.elements[index].duration.value
                meters += distanceMatrixRow.elements[index].distance.value
            }
        } catch (e: Exception) {
            _distanceMatrixStatus.value = Event(Resource.error(CALCULUS_ERROR, null))
            _mileage.value = INITIAL_LONG.toString()
            _time.value = INITIAL_TIME
            return
        }

        setMileage(meters)
        setHours(seconds)

        prefs.putString(ROUTE_LIST, routesToJson())
        prefs.putString(MATRIX_MILEAGE, mileage.value.replace(" km", ""))
        prefs.putString(MATRIX_TIME, time.value)
        prefs.commit()
    }

    private fun setMileage(meters: Long) {
        _mileage.value = if (meters == 0L) {
            "0"
        } else {
            "${ceil(meters.toDouble() / METERS_IN_KM).toLong()}"
        }
    }

    private fun setHours(seconds: Long) {
        val horas = seconds / SECONDS_IN_HOUR
        val diff = if (horas > 0) {
            seconds % (horas * SECONDS_IN_HOUR)
        } else {
            seconds % SECONDS_IN_HOUR
        }
        val minutes = ceil(diff.toDouble() / SECONDS_IN_MINUTE).toLong()

        _time.value = "${horas}h $minutes min"
    }

    fun getRoutes() = viewModelScope.launch(dispatcher.io) {
        _distanceMatrixStatus.value = Event(Resource.loading(null))
        _recyclerList.emit(Resource.loading(null))
        repository.getAllRoutes().collectLatest {
            _routes.value = it
            _recyclerList.emit(Resource.success(it))

            checkUpdates()
        }
    }

    private suspend fun checkUpdates() {
        val savedListString = sharedPreferences.getString(ROUTE_LIST, "")
        val jsonRoutes = routesToJson()

        if (savedListString != jsonRoutes && routes.value.size > 1) {
            return getDistanceMatrix()
        }
        if (routes.value.size > 1) {
            return getStoredPrefs()
        } else {
            resetDashboard()
        }
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
        _mileage.value = sharedPreferences.getString(MATRIX_MILEAGE, "0").toString()
        _time.value = sharedPreferences.getString(MATRIX_TIME, INITIAL_TIME)!!
    }

    private fun resetDashboard() {
        _mileage.value = INITIAL_LONG.toString()
        _time.value = INITIAL_TIME
    }

    private suspend fun getDistanceMatrix() {
        if ((routes.value.size) <= TWO_ELEMENTS) {
            _distanceMatrixStatus.value =
                Event(Resource.error(FEW_ELEMENTS_ERROR, null))
            return
        }
        _distanceMatrixStatus.value = Event(Resource.loading(null))

        val response = repository.getDistanceMatrix(routes.value)

        _distanceMatrixStatus.value = Event(response)

    }

    fun isFirstRoute(): Boolean = _routes.value.isEmpty()
}