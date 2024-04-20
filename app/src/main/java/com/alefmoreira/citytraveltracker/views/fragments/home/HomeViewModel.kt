package com.alefmoreira.citytraveltracker.views.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.model.Dashboard
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.other.Constants.TWO_ELEMENTS
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
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
) : ViewModel() {

    private var _networkStatus = MutableStateFlow(NetworkObserver.NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkObserver.NetworkStatus> = _networkStatus

    var isFirstRoute = true

    private var _recyclerList = MutableStateFlow<Resource<List<Route>>>(Resource.init())
    val recyclerList: SharedFlow<Resource<List<Route>>> = _recyclerList

    private var _dasboardStatus = MutableStateFlow(Resource(Status.INIT, Dashboard(), null))
    val dashboardStatus: MutableStateFlow<Resource<Dashboard>> = _dasboardStatus

    init {
        checkNetwork()
        observeNetwork()
        observeRoutesToUpdateDasboard()
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

    private fun observeRoutesToUpdateDasboard() = viewModelScope.launch(dispatcher.io) {
        recyclerList.collect {
            it.data?.let { list ->
                if (list.size >= TWO_ELEMENTS) {
                    _dasboardStatus.value = Resource.loading()
                    _dasboardStatus.value = repository.getDashboard(list)
                } else {
                    resetDashboard()
                }
            }
        }
    }

    private fun resetDashboard() {
        _dasboardStatus.value.data?.reset()
    }

    fun getRoutes() = viewModelScope.launch(dispatcher.io) {
        _dasboardStatus.value = Resource.loading()
        _recyclerList.emit(Resource.loading())
        val list = repository.getAllRoutes()
        isFirstRoute = list.isEmpty()
        _recyclerList.emit(Resource.success(list))
    }
}