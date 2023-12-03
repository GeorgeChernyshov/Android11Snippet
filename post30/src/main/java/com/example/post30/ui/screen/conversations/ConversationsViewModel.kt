package com.example.post30.ui.screen.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ConversationsViewModel : ViewModel() {

    private val _state = MutableStateFlow(ConversationsScreenState.DEFAULT)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            DIUtils.replyRepository.replyText.collect {
                _state.emit(_state.value.copy(response = it))
            }
        }
    }
}