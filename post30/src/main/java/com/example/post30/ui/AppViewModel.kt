package com.example.post30.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.post30.ui.navigation.Screen
import java.util.Stack

class AppViewModel : ViewModel() {

    private val _screenStack: Stack<Screen> = Stack()
    private val _currentScreen: MutableState<Screen> = mutableStateOf(Screen.Main)
    val currentScreen: State<Screen> = _currentScreen

    private val _appOps: MutableState<List<String>> = mutableStateOf(emptyList())
    val appOps: State<List<String>>
        get() = _appOps
    
    init {
        _screenStack.push(Screen.Main)
    }

    fun navigateTo(screen: Screen) {
        _screenStack.push(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.size > 1) {
            _screenStack.pop()
            _currentScreen.value = _screenStack.peek()

            return true
        }

        return false
    }

    fun addAppOp(op: String) {
        _appOps.value += op
    }
}