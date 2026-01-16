package com.example.post30.ui.screen.privacy.location

data class LocationScreenState(
    val foregroundLocationPermitted: Boolean,
    val backgroundLocationPermitted: Boolean
) {
    companion object {
        val DEFAULT = LocationScreenState(
            foregroundLocationPermitted = false,
            backgroundLocationPermitted = false
        )
    }
}