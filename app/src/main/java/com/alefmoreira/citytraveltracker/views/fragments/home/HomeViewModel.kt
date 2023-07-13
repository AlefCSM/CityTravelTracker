package com.alefmoreira.citytraveltracker.views.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.other.Event
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    private var _kilometers = MutableStateFlow(0.0)
    val kilometers: StateFlow<Double> = _kilometers

    private var _hours = MutableStateFlow(0.0)
    val hours: StateFlow<Double> = _hours

    private var _networkStatus = MutableStateFlow(NetworkObserver.NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkObserver.NetworkStatus> = _networkStatus

    private var _routes =
        MutableStateFlow<List<Route>>(mutableListOf())

    val routes: StateFlow<List<Route>> = _routes

    private var _routeStatus = MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))
    val routeStatus: StateFlow<Event<Resource<Route>>> = _routeStatus

    private var _distanceMatrixStatus = MutableStateFlow<Event<Resource<DistanceMatrixResponse>>>(
        Event(
            Resource.init()
        )
    )

    var distanceMatrixStatus: MutableStateFlow<Event<Resource<DistanceMatrixResponse>>> =
        _distanceMatrixStatus

    private var _originStatus =
        MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val originStatus: StateFlow<Event<Resource<Route>>> = _originStatus

    private var _destinationStatus =
        MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val destinationStatus: StateFlow<Event<Resource<Route>>> = _destinationStatus


    init {
        getRoutes()
        checkNetwork()
        observeNetwork()
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

    private fun getRoutes() = viewModelScope.launch(dispatcher.io) {
        repository.getAllRoutes().collectLatest {
            _routes.value = it
        }
    }

    fun getDistanceMatrix() {
        if ((routes.value.size) <= 1) {
            _distanceMatrixStatus.value =
                Event(Resource.error("There must be at least 2 routes.", null))
            return
        }
        _distanceMatrixStatus.value = Event(Resource.loading(null))

        viewModelScope.launch(dispatcher.main) {
            val response = repository.getDistanceMatrix(getOrigins(), getDestinations())
            _distanceMatrixStatus.value = Event(response)
        }

        _distanceMatrixStatus.value = Event(
            Resource.success(
                DistanceMatrixResponse(
                    emptyList(),
                    emptyList(), emptyList(), ""
                )
            )
        )
    }

    fun isFirstRoute(): Boolean = _routes.value.isEmpty()

    fun getOrigins(): List<Route> = routes.value.dropLast(1)

    fun getDestinations(): List<Route> = routes.value.drop(1)
}