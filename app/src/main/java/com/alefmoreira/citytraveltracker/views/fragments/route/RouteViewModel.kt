package com.alefmoreira.citytraveltracker.views.fragments.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.other.Constants.DEFAULT_CONNECTION_POSITION
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    private val _networkStatus = MutableStateFlow(NetworkObserver.NetworkStatus.Available)

    val networkStatus: StateFlow<NetworkObserver.NetworkStatus> = _networkStatus

    var currentDestination = Route(City(null, "", ""), mutableListOf())
        private set
    var currentOrigin = Route(City(null, "", ""), mutableListOf())
        private set

    private var _routeStatus = MutableSharedFlow<Resource<Route>>()
    val routeStatus: SharedFlow<Resource<Route>> = _routeStatus

    private var _originStatus = MutableSharedFlow<Resource<Route>>()

    private var _destinationStatus =
        MutableSharedFlow<Resource<Route>>()

    val destinationStatus: SharedFlow<Resource<Route>> = _destinationStatus

    var isLoading: Boolean = false
        private set

    var isFirstRoute = MutableStateFlow(true)

    init {
        setupObservers()
        observeNetwork()
    }

    private fun observeNetwork() = viewModelScope.launch(dispatcher.io) {
        networkObserver.observe().collectLatest {
            _networkStatus.value = it
        }
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

    fun addConnection(name: String, placeId: String, position: Int) =
        viewModelScope.launch(dispatcher.main) {
            if (!validateRoute(name, placeId)) {
                _destinationStatus.emit(Resource.error("The fields must not be empty!", null))
                return@launch
            }

            if (currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()) {
                _destinationStatus.emit(Resource.error("The city must not be empty!", null))
                return@launch
            }

            val connection = Connection(cityId = 0, name = name, placeId = placeId)

            if (currentDestination.connections.isNotEmpty() && position > DEFAULT_CONNECTION_POSITION) {
                currentDestination.connections[position] = connection
            } else {
                currentDestination.connections.add(connection)
            }
            _destinationStatus.emit(Resource.success(currentDestination))
        }

    fun removeConnection(connection: Connection) = viewModelScope.launch(dispatcher.main) {
        currentDestination.connections.remove(connection)
        if (currentDestination.connections.isEmpty()) {
            _destinationStatus.emit(Resource.success(currentDestination))
        }
    }


    private fun validateRoute(name: String, placeId: String): Boolean =
        name.isNotEmpty() && placeId.isNotEmpty()


    fun saveRoute() = viewModelScope.launch(dispatcher.main) {
        _routeStatus.emit(Resource.loading(null))
        if (isFirstRoute.value && isOriginEmpty()) {
            _routeStatus.emit(Resource.error("The origin must not be empty.", null))
            return@launch
        }
        if (isDestinationEmpty()) {
            _routeStatus.emit(Resource.error("The destination must not be empty.", null))
            return@launch
        }

        if (isFirstRoute.value) {
            insertDestinationIntoDB(currentOrigin)
        }

        insertDestinationIntoDB(currentDestination).join()
        _routeStatus.emit(Resource.success(currentDestination))
    }

    private fun insertDestinationIntoDB(route: Route) = viewModelScope.launch(dispatcher.main) {
        repository.insertRoute(route)
    }

    fun clearRoutes() {
        currentOrigin = Route(City(name = "", placeId = ""), mutableListOf())
        currentDestination = Route(City(name = "", placeId = ""), mutableListOf())
    }


    fun deleteRoute(route: Route) = viewModelScope.launch(dispatcher.io) {
        _routeStatus.emit(Resource.loading(null))
        repository.deleteRoute(route)
        _routeStatus.emit(Resource.success(route))
    }

    private fun isOriginEmpty(): Boolean =
        currentOrigin.city.name.isEmpty() || currentOrigin.city.placeId.isEmpty()

    fun isDestinationEmpty(): Boolean =
        currentDestination.city.name.isEmpty() || currentDestination.city.placeId.isEmpty()

    fun isButtonEnabled(): Boolean {
        return if (isFirstRoute.value) {
            isOriginEmpty().not() && isDestinationEmpty().not()
        } else {
            isDestinationEmpty().not()
        }
    }

    private fun setupObservers() = viewModelScope.launch(dispatcher.main) {
        routeStatus.collect { isLoading = it.status == Status.LOADING }
    }

    fun getRoute(id: Long) = viewModelScope.launch(dispatcher.io) {
        _destinationStatus.emit(Resource.loading(null))

        currentDestination = repository.getRouteById(id)
        _destinationStatus.emit(Resource.success(currentDestination))
    }
}