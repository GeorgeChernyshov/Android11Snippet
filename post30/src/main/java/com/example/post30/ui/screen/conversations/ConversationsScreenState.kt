package com.example.post30.ui.screen.conversations

data class ConversationsScreenState(
    val response: String?
) {
    companion object {
        val DEFAULT = ConversationsScreenState(null)
    }
}