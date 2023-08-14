package com.alefmoreira.citytraveltracker.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

class NetworkObserverImpl @Inject constructor(
    context: Context,
    private val dispatcher: DispatcherProvider
) : NetworkObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<NetworkObserver.NetworkStatus> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    handleInternet()
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch(dispatcher.io) { send(NetworkObserver.NetworkStatus.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch(dispatcher.io) { send(NetworkObserver.NetworkStatus.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch(dispatcher.io) { send(NetworkObserver.NetworkStatus.Unavailable) }
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    super.onLinkPropertiesChanged(network, linkProperties)

                    handleInternet()
                }

                private fun handleInternet() {
                    if (isInternetAvailable()) {
                        launch(dispatcher.io) { send(NetworkObserver.NetworkStatus.Available) }
                    } else {
                        launch(dispatcher.io) { send(NetworkObserver.NetworkStatus.Unavailable) }
                    }
                }

            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    override fun isConnected(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities?.let {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }
}