package com.alefmoreira.citytraveltracker.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkObserverTest : NetworkObserver {

    private var connected: Boolean = false
    private var networkStatus: NetworkObserver.NetworkStatus =
        NetworkObserver.NetworkStatus.Available
    private var networkObserver = MutableStateFlow(networkStatus)

    override fun observe(): Flow<NetworkObserver.NetworkStatus> {
        return networkObserver
    }

    override fun isConnected(): Boolean {
        return connected
    }

    fun setNetworkStatus(status: NetworkObserver.NetworkStatus){
        networkStatus = status
        networkObserver.value = networkStatus
    }
}