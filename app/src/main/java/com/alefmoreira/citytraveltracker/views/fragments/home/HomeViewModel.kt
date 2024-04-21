package com.alefmoreira.citytraveltracker.views.fragments.home

import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.model.Dashboard
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.other.Constants.TWO_ELEMENTS
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import com.alefmoreira.citytraveltracker.views.fragments.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider,
    private val networkObserver: NetworkObserver
) : BaseViewModel() {

    private var _networkStatus = MutableStateFlow(NetworkObserver.NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkObserver.NetworkStatus> = _networkStatus

    var isFirstRoute = true

    private var _routes = MutableStateFlow<Resource<List<Route>>>(Resource.init())
    val routes: SharedFlow<Resource<List<Route>>> = _routes

    private var _dashboardStatus = MutableStateFlow(Resource(Status.INIT, Dashboard(), null))
    val dashboardStatus: MutableStateFlow<Resource<Dashboard>> = _dashboardStatus

    init {
        checkNetwork()
        observeNetwork()
        observeRoutesToUpdateDashboard()
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

    private fun observeRoutesToUpdateDashboard() = viewModelScope.launch(dispatcher.io) {
        routes.collect {
            it.data?.let { list ->
                if (list.size >= TWO_ELEMENTS) {
                    _dashboardStatus.value = Resource.loading()
                    _dashboardStatus.value = repository.getDashboard(list)
                } else {
                    resetDashboard()
                }
            }
        }
    }

    private fun resetDashboard() {
        _dashboardStatus.value.data?.reset()
    }

    fun getRoutes() = viewModelScope.launch(dispatcher.io) {
        _dashboardStatus.value = Resource.loading()
        _routes.emit(Resource.loading())
        val list = repository.getAllRoutes()
        isFirstRoute = list.isEmpty()
        _routes.emit(Resource.success(list))
    }
}