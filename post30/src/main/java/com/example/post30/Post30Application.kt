package com.example.post30

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.post30.ui.screen.conversations.DIUtils

class Post30Application : Application() {

    override fun onCreate() {
        super.onCreate()
        DIUtils.init()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannelGroup(
                NotificationChannelGroup(CHAT_GROUP, CHAT_GROUP)
            )

            val name = getString(R.string.notification_chat_channel_name)
            val descriptionText = getString(R.string.notification_chat_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHAT_CHANNEL, name, importance).apply {
                description = descriptionText
                group = CHAT_GROUP
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHAT_GROUP = "chatGroup"
        const val CHAT_CHANNEL = "chatChannel"
    }
}