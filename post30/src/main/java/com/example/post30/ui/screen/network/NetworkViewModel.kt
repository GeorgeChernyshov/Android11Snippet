package com.example.post30.ui.screen.network

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

    fun setConnectionType(
        connectionType: ConnectionType
    ) = viewModelScope.launch {
        _state.emit(_state.value.copy(connectionType = connectionType))
    }

    fun setWiFiConnectionStatus(
        status: ConnectionStatus?
    ) = viewModelScope.launch {
        _state.emit(_state.value.copy(wifiConnectionStatus = status))
    }

    fun setCellularConnectionStatus(
        status: ConnectionStatus?
    ) = viewModelScope.launch {
        _state.emit(_state.value.copy(cellularConnectionStatus = status))
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