package com.alefmoreira.citytraveltracker.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Event
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CTTViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {


    private var currentDestination = Route(City(null, "", ""), mutableListOf())
    private var currentOrigin = Route(City(null, "", ""), mutableListOf())


    private var _routes =
        MutableStateFlow<Event<Resource<List<Route>>>>(Event(Resource.init()))

    val routes: StateFlow<Event<Resource<List<Route>>>> = _routes

    private var _routeStatus = MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))
    val routeStatus: StateFlow<Event<Resource<Route>>> = _routeStatus

    private var _distanceMatrixStatus = MutableStateFlow<Event<Resource<DistanceMatrixResponse>>>(
        Event(
            Resource.init())
    )

    var distanceMatrixStatus: MutableStateFlow<Event<Resource<DistanceMatrixResponse>>> = _distanceMatrixStatus

    private var _originStatus =
        MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val originStatus: StateFlow<Event<Resource<Route>>> = _originStatus

    private var _destinationStatus =
        MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val destinationStatus: StateFlow<Event<Resource<Route>>> = _destinationStatus


    init {
        observeRoutes()
    }

    fun setOrigin(name: String, placeId: String) {
        if (!validateRoute(name, placeId)) {
            _originStatus.value = Event(Resource.error("The fields must not be empty!", null))
            return
        }
        currentOrigin.city.name = name
        currentOrigin.city.placeId = placeId
        _originStatus.value = Event(Resource.success(currentOrigin))
    }

    fun setDestination(name: String, placeId: String) {
        if (!validateRoute(name, placeId)) {
            _destinationStatus.value = Event(Resource.error("The fields must not be empty!", null))
            return
        }
        currentDestination.city.name = name
        currentDestination.city.placeId = placeId
        _destinationStatus.value = Event(Resource.success(currentDestination))
    }

    fun addConnection(name: String, placeId: String) {
        if (!validateRoute(name, placeId)) {
            _destinationStatus.value = Event(Resource.error("The fields must not be empty!", null))
            return
        }

        if (currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()) {
            _destinationStatus.value = Event(Resource.error("The city must not be empty!", null))
            return
        }

        val connection = Connection(cityId = 0, name = name, placeId = placeId)

        currentDestination.connections.add(connection)

        _destinationStatus.value = Event(Resource.success(currentDestination))
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

    private fun clearRoutes(){
        currentOrigin = Route(City(name = "", placeId = ""), mutableListOf())
        currentDestination = Route(City(name = "", placeId = ""), mutableListOf())
    }

    private fun observeRoutes() = viewModelScope.launch(dispatcher.main) {
        repository.observeAllRoutes().collectLatest {
            _routes.value = Event(Resource.success(it))
        }
    }

    fun deleteRoute(route: Route) = viewModelScope.launch(dispatcher.main) {
        if (_routeStatus.value.peekContent().status != Status.LOADING){
            _routeStatus.value = Event(Resource.loading(null))
            repository.deleteRoute(route)
            _routeStatus.value = Event(Resource.success(route))
        }

    }

    fun getDistanceMatrix() {
        if ((routes.value.peekContent().data?.size ?: 0) <= 1) {
            _distanceMatrixStatus.value = Event(Resource.error("There must be at least 2 routes.", null))
            return
        }
        _distanceMatrixStatus.value = Event(Resource.loading(null))

        viewModelScope.launch(dispatcher.main) {
            val response = repository.getDistanceMatrix(getOrigins(),getDestinations())
            _distanceMatrixStatus.value = Event(response)
        }

        _distanceMatrixStatus.value = Event(Resource.success(DistanceMatrixResponse(emptyList(),
            emptyList(), emptyList(),"")))
    }

    fun isFirstRoute(): Boolean = _routes.value.peekContent().data?.isEmpty() ?: true
    fun isOriginEmpty(): Boolean =
        currentOrigin.city.name.isEmpty() || currentOrigin.city.placeId.isEmpty()

    fun isDestinationEmpty(): Boolean =
        currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()

    fun getOrigins():List<Route> = routes.value.peekContent().data?.dropLast(1) ?: emptyList()
    fun getDestinations():List<Route> = routes.value.peekContent().data?.drop(1) ?: emptyList()
}