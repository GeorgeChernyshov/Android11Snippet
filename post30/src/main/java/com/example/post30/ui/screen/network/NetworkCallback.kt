package com.example.post30.ui.screen.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED
import android.net.NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED

class NetworkCallback(
    private val setMeteredness: (NetworkScreenState.Meteredness?) -> Unit,
    private val setBandwidth: (Int?, Int?) -> Unit
) : ConnectivityManager.NetworkCallback() {
    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)

        val meteredness = when {
            networkCapabilities.hasCapability(NET_CAPABILITY_NOT_METERED) -> NetworkScreenState.Meteredness.UNMETERED

            (networkCapabilities.hasCapability(NET_CAPABILITY_TEMPORARILY_NOT_METERED)) -> {
                NetworkScreenState.Meteredness.TEMPORARILY_UNMETERED
            }

            else -> NetworkScreenState.Meteredness.METERED
        }

        setMeteredness(meteredness)
        setBandwidth(
            networkCapabilities.linkDownstreamBandwidthKbps,
            networkCapabilities.linkUpstreamBandwidthKbps
        )
    }
}