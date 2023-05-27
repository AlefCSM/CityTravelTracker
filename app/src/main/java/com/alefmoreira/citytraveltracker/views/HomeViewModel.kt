package com.alefmoreira.citytraveltracker.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.data.City
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
class HomeViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {


    private lateinit var currentRoute: Route


    private var _routes =
        MutableStateFlow<Event<Resource<List<Route>>>>(Event(Resource.init()))

    val observableRoutes: StateFlow<Event<Resource<List<Route>>>> = _routes

    private var origins: List<Route> = emptyList()

    private var destinations: List<Route> = emptyList()

    private var _routeStatus =
        MutableStateFlow<Event<Resource<Route>>>(Event(Resource.init()))

    val routeStatus: StateFlow<Event<Resource<Route>>> = _routeStatus


    private val _distanceMatrix = MutableStateFlow<Event<Resource<DistanceMatrixResponse>>>(
        Event(Resource.init())
    )

    val distaceMatric: StateFlow<Event<Resource<DistanceMatrixResponse>>> = _distanceMatrix


    init {
        observeRoutes()
    }

    fun createDestination(name: String, placeId: String) {
        _routeStatus.value = Event(Resource.loading(null))

        if (name.isEmpty() || placeId.isEmpty()) {
            _routeStatus.value =
                Event(Resource.error("The fields must not be empty!", null))
            return
        }

        val city = City(name = name, placeId = placeId)

        val route = Route(city, mutableListOf())

        insertDestinationIntoDB(route)

        _routeStatus.value = Event(Resource(Status.SUCCESS, route, ""))
    }

    fun insertDestinationIntoDB(route: Route) = viewModelScope.launch(dispatcher.main) {
        repository.insertRoute(route)
    }

    private fun observeRoutes() = viewModelScope.launch(dispatcher.main) {
        repository.observeAllRoutes().collectLatest {
            _routes.value = Event(Resource.success(it))
            origins = it.dropLast(1)
            destinations = it.drop(1)
        }
    }

    fun deleteDestination(route: Route) = viewModelScope.launch(dispatcher.main) {

        repository.deleteRoute(route)
    }

    fun getDistanceMatrix() {
        if (origins.isEmpty() || destinations.isEmpty()) {
            return
        }


    }

    fun isFirstRoute(): Boolean = _routes.value.peekContent().data?.isEmpty() ?: true
}