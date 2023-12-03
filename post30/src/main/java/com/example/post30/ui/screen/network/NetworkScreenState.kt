package com.example.post30.ui.screen.network

data class NetworkScreenState(
    val meteredness: Meteredness?,
    val fiveGStatus: Int?,
    val downstreamBandwidth: Int?,
    val upstreamBandwidth: Int?
) {
    enum class Meteredness {
        METERED,
        UNMETERED,
        TEMPORARILY_UNMETERED;
    }

    companion object {
        val DEFAULT = NetworkScreenState(
            meteredness = null,
            fiveGStatus = null,
            downstreamBandwidth = null,
            upstreamBandwidth = null
        )
    }
}