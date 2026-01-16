package com.example.post30.ui.screen.privacy.location

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {

    private val _state = mutableStateOf(LocationScreenState.DEFAULT)
    val state: State<LocationScreenState> = _state

    fun updatePermissions(
        foregroundLocationPermitted: Boolean? = null,
        backgroundLocationPermitted: Boolean? = null
    ) {
        _state.value = _state.value.copy(
            foregroundLocationPermitted = foregroundLocationPermitted
                ?: _state.value.foregroundLocationPermitted,
            backgroundLocationPermitted = backgroundLocationPermitted
                ?: _state.value.backgroundLocationPermitted
        )
    }
}