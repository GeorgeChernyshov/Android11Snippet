package com.example.post30.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.post30.ui.navigation.Screen

class AppViewModel : ViewModel() {

    private val _currentScreen = mutableStateOf(Screen.Storage)
    val currentScreen: State<Screen>
        get() = _currentScreen
}