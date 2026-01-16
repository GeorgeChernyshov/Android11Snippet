package com.example.post30.ui.screen.newfeatures.conversations

data class ConversationsScreenState(
    val response: String?
) {
    companion object {
        val DEFAULT = ConversationsScreenState(null)
    }
}