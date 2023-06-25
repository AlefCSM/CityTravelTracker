package com.alefmoreira.citytraveltracker.views.fragments.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Resource.Companion.init
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    var currentDestination = Route(City(null, "", ""), mutableListOf())
        private set
    var currentOrigin = Route(City(null, "", ""), mutableListOf())
        private set

    private var _routes = mutableListOf<Route>()

    val routes: List<Route> = _routes

    private var _routeStatus = MutableSharedFlow<Resource<Route>>()
    val routeStatus: SharedFlow<Resource<Route>> = _routeStatus

    private var _originStatus = MutableSharedFlow<Resource<Route>>()

    val originStatus: SharedFlow<Resource<Route>> = _originStatus

    private var _destinationStatus =
        MutableStateFlow<Resource<Route>>(init())

    val destinationStatus: StateFlow<Resource<Route>> = _destinationStatus

    var isLoading: Boolean = false
        private set

    init {
        observeRoutes()
        setupObservers()
    }

    fun setOrigin(name: String, placeId: String) = viewModelScope.launch(dispatcher.main) {
        if (!validateRoute(name, placeId)) {
            _originStatus.emit(Resource.error("The fields must not be empty!", null))
            return@launch
        }
        currentOrigin.city.name = name
        currentOrigin.city.placeId = placeId
        _originStatus.emit(Resource.success(currentOrigin))
    }

    fun setDestination(name: String, placeId: String) = viewModelScope.launch(dispatcher.main) {
        if (!validateRoute(name, placeId)) {
            _destinationStatus.emit(Resource.error("The fields must not be empty!", null))
            return@launch
        }
        currentDestination.city.name = name
        currentDestination.city.placeId = placeId
        _destinationStatus.emit(Resource.success(currentDestination))
    }

    fun addConnection(name: String, placeId: String) = viewModelScope.launch(dispatcher.main) {
        if (!validateRoute(name, placeId)) {
            _destinationStatus.emit(Resource.error("The fields must not be empty!", null))
            return@launch
        }

        if (currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()) {
            _destinationStatus.emit(Resource.error("The city must not be empty!", null))
            return@launch
        }

        val connection = Connection(cityId = 0, name = name, placeId = placeId)

        currentDestination.connections.add(connection)

        _destinationStatus.emit(Resource.success(currentDestination))
    }


    private fun validateRoute(name: String, placeId: String): Boolean =
        name.isNotEmpty() && placeId.isNotEmpty()


    fun saveRoute() = viewModelScope.launch(dispatcher.main) {
        _routeStatus.emit(Resource.loading(null))
        if (isFirstRoute() && isOriginEmpty()) {
            _routeStatus.emit(Resource.error("The origin must not be empty.", null))
            return@launch
        }
        if (isDestinationEmpty()) {
            _routeStatus.emit(Resource.error("The destination must not be empty.", null))
            return@launch
        }

        if (isFirstRoute()) {
            insertDestinationIntoDB(currentOrigin)
        }

        insertDestinationIntoDB(currentDestination)
        _routeStatus.emit(Resource.success(currentDestination))
        clearRoutes()
    }

    private fun insertDestinationIntoDB(route: Route) = viewModelScope.launch(dispatcher.main) {
        repository.insertRoute(route)
    }

    fun clearRoutes() {
        currentOrigin = Route(City(name = "", placeId = ""), mutableListOf())
        currentDestination = Route(City(name = "", placeId = ""), mutableListOf())
    }

    private fun observeRoutes() = viewModelScope.launch(dispatcher.main) {
        repository.observeAllRoutes().collectLatest {
            _routes = it.toMutableList()
        }
    }

    fun deleteRoute(route: Route) = viewModelScope.launch(dispatcher.main) {
        _routeStatus.emit(Resource.loading(null))
        repository.deleteRoute(route)
        _routeStatus.emit(Resource.success(route))
    }

    fun isFirstRoute(): Boolean = _routes.isEmpty()
    private fun isOriginEmpty(): Boolean =
        currentOrigin.city.name.isEmpty() || currentOrigin.city.placeId.isEmpty()

    fun isDestinationEmpty(): Boolean =
        currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()

    fun isButtonEnabled(): Boolean {
        return if (isFirstRoute()) {
            isOriginEmpty().not() && isDestinationEmpty().not()
        } else {
            isDestinationEmpty().not()
        }
    }

    private fun setupObservers() = viewModelScope.launch(dispatcher.main) {
        routeStatus.collect { isLoading = it.status == Status.LOADING }
    }
}