package com.devilgamehub.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkObserver private constructor(context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnline = MutableStateFlow(checkInitialConnectivity())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        registerCallback()
    }

    private fun checkInitialConnectivity(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (_: Exception) {
            false
        }
    }

    private fun registerCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    updateConnectivityState()
                }

                override fun onLost(network: Network) {
                    updateConnectivityState()
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    _isOnline.value = isValidated
                }
            })
        } catch (_: Exception) {
            _isOnline.value = checkInitialConnectivity()
        }
    }

    private fun updateConnectivityState() {
        _isOnline.value = checkInitialConnectivity()
    }

    companion object {
        @Volatile
        private var INSTANCE: NetworkObserver? = null

        fun getInstance(context: Context): NetworkObserver {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkObserver(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
