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


    private var currentDestination = Route(City(null, "", ""), mutableListOf())
    private var currentOrigin = Route(City(null, "", ""), mutableListOf())

    private var _networkStatus = MutableStateFlow(NetworkObserver.NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkObserver.NetworkStatus> = _networkStatus

    private var _routes =
        MutableStateFlow<Event<Resource<List<Route>>>>(Event(Resource.init()))

    val routes: StateFlow<Event<Resource<List<Route>>>> = _routes

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


    private fun validateRoute(name: String, placeId: String): Boolean =
        name.isNotEmpty() && placeId.isNotEmpty()


    fun saveRoute() {
        if (_routeStatus.value.peekContent().status != Status.LOADING) {
            _routeStatus.value = Event(Resource.loading(null))
            if (isFirstRoute() && isOriginEmpty()) {
                _routeStatus.value = Event(Resource.error("The origin must not be empty.", null))
                return
            }
            if (isDestinationEmpty()) {
                _routeStatus.value =
                    Event(Resource.error("The destination must not be empty.", null))
                return
            }

            if (isFirstRoute()) {
                insertDestinationIntoDB(currentOrigin)
            }

            insertDestinationIntoDB(currentDestination)
            _routeStatus.value = Event(Resource.success(currentDestination))
            clearRoutes()
        }
    }

    private fun insertDestinationIntoDB(route: Route) = viewModelScope.launch(dispatcher.main) {
        repository.insertRoute(route)
    }

    fun clearRoutes() {
        currentOrigin = Route(City(name = "", placeId = ""), mutableListOf())
        currentDestination = Route(City(name = "", placeId = ""), mutableListOf())
    }

    fun getRoutes() = viewModelScope.launch(dispatcher.io) {
        repository.getAllRoutes().collectLatest {
            _routes.value = Event(Resource.success(it))
        }
    }

    fun deleteRoute(route: Route) = viewModelScope.launch(dispatcher.main) {
        if (_routeStatus.value.peekContent().status != Status.LOADING) {
            _routeStatus.value = Event(Resource.loading(null))
            repository.deleteRoute(route)
            _routeStatus.value = Event(Resource.success(route))
        }

    }

    fun getDistanceMatrix() {
        if ((routes.value.peekContent().data?.size ?: 0) <= 1) {
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

    fun isFirstRoute(): Boolean = _routes.value.peekContent().data?.isEmpty() ?: false
    fun isOriginEmpty(): Boolean =
        currentOrigin.city.name.isEmpty() || currentOrigin.city.placeId.isEmpty()

    fun isDestinationEmpty(): Boolean =
        currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()

    fun getOrigins(): List<Route> = routes.value.peekContent().data?.dropLast(1) ?: emptyList()
    fun getDestinations(): List<Route> = routes.value.peekContent().data?.drop(1) ?: emptyList()
}