package com.example.post30.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.post30.ui.navigation.Screen

class AppViewModel : ViewModel() {

    private val _currentScreen: MutableState<Screen> = mutableStateOf(Screen.Storage)
    val currentScreen: State<Screen>
        get() = _currentScreen

    private val _appOps: MutableState<List<String>> = mutableStateOf(emptyList())
    val appOps: State<List<String>>
        get() = _appOps

    fun setCurrentScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    fun addAppOp(op: String) {
        _appOps.value += op
    }
}