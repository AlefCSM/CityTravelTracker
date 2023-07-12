package com.alefmoreira.citytraveltracker.network

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {

    fun observe(): Flow<NetworkStatus>

    fun isConnected(): Boolean

    enum class NetworkStatus {
        Available, Unavailable, Losing, Lost
    }
}