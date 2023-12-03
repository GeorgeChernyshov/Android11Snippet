package com.example.post30.ui.screen.conversations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReplyRepository {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job )

    private val _replyText: MutableStateFlow<String?> = MutableStateFlow(null)
    val replyText = _replyText.asStateFlow()

    fun setReplyText(text: String?) = coroutineScope.launch {
        _replyText.emit(text)
    }
}