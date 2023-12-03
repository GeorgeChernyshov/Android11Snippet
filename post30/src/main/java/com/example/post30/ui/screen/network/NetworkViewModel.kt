package com.example.post30.ui.screen.network

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkViewModel : ViewModel() {

    private val _state = MutableStateFlow(NetworkScreenState.DEFAULT)
    val state = _state.asStateFlow()

    fun setMeteredness(value: NetworkScreenState.Meteredness?) = viewModelScope.launch {
        _state.emit(_state.value.copy(meteredness = value))
    }

    fun setFiveGStatus(status: Int?) = viewModelScope.launch {
        _state.emit(_state.value.copy(fiveGStatus = status))
    }

    fun setBandwidth(
        downstreamBandwidth: Int?,
        upstreamBandwidth: Int?
    ) = viewModelScope.launch {
        _state.emit(_state.value.copy(
            downstreamBandwidth = downstreamBandwidth,
            upstreamBandwidth = upstreamBandwidth
        ))
    }
}