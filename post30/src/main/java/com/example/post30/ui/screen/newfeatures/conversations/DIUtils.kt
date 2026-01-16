package com.example.post30.ui.screen.newfeatures.conversations

object DIUtils {
    lateinit var replyRepository: ReplyRepository

    fun init() {
        replyRepository = ReplyRepository()
    }
}