package com.example.post30.ui.screen.permissions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PermissionsViewModel : ViewModel() {

    private val _state = mutableStateOf(PermissionsScreenState.DEFAULT)
    val state: State<PermissionsScreenState> = _state

    fun getPhoneNumber(number: String?) {
        _state.value = _state.value.copy(line1Number = number)
    }
}