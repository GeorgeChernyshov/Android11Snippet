package com.example.post30.ui.screen.privacy.storage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class StorageViewModel : ViewModel() {

    private val _state = mutableStateOf(StorageScreenState.DEFAULT)
    val state: State<StorageScreenState> = _state

    fun showFavoriteUnavailable() {
        _state.value = _state.value.copy(showFavoriteUnavailableText = true)
    }
}