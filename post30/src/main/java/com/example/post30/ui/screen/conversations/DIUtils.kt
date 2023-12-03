package com.example.post30.ui.screen.conversations

object DIUtils {
    lateinit var replyRepository: ReplyRepository

    fun init() {
        replyRepository = ReplyRepository()
    }
}