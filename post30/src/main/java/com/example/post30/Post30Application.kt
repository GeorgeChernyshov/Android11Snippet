package com.example.post30

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.post30.ui.screen.newfeatures.conversations.DIUtils

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

            notificationManager.createNotificationChannelGroup(
                NotificationChannelGroup(LOUD_GROUP, LOUD_GROUP)
            )

            val chatChannel = NotificationChannel(
                CHAT_CHANNEL,
                getString(R.string.notification_chat_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_chat_channel_description)
                group = CHAT_GROUP
            }

            val loudChannel = NotificationChannel(
                LOUD_CHANNEL,
                getString(R.string.notification_loud_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_loud_channel_description)
                group = LOUD_GROUP
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            // Register the channel with the system
            notificationManager.createNotificationChannel(chatChannel)
            notificationManager.createNotificationChannel(loudChannel)
        }
    }

    companion object {
        private const val CHAT_GROUP = "chatGroup"
        const val CHAT_CHANNEL = "chatChannel"

        private const val LOUD_GROUP = "loudGroup"
        const val LOUD_CHANNEL = "loudChannel"
    }
}