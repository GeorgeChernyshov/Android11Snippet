package com.example.post30.ui.screen.network

data class NetworkScreenState(
    val meteredness: Meteredness?,
    private val connectionType: ConnectionType,
    val cellularConnectionStatus: ConnectionStatus?,
    val wifiConnectionStatus: ConnectionStatus?,
    val downstreamBandwidth: Int?,
    val upstreamBandwidth: Int?
) {

    val connectionStatus: ConnectionStatus?
        get() = when (connectionType) {
            ConnectionType.WIFI -> wifiConnectionStatus
            ConnectionType.CELLULAR -> cellularConnectionStatus
            ConnectionType.NO_CONNECTION -> ConnectionStatus.NO_CONNECTION
            ConnectionType.UNKNOWN -> null
        }

    enum class Meteredness {
        METERED,
        UNMETERED,
        TEMPORARILY_UNMETERED;
    }

    companion object {
        val DEFAULT = NetworkScreenState(
            meteredness = null,
            connectionType = ConnectionType.UNKNOWN,
            cellularConnectionStatus = null,
            wifiConnectionStatus = null,
            downstreamBandwidth = null,
            upstreamBandwidth = null
        )
    }
}