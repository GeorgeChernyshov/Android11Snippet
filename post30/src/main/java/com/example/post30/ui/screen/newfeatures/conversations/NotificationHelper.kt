package com.example.post30.ui.screen.newfeatures.conversations

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BubbleMetadata
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.post30.Post30Application
import com.example.post30.R
import com.example.post30.ui.bubbles.BubbleActivity

class NotificationHelper(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.R)
    fun showChatNotification() {
        val sender = Person.Builder()
            .setName("Carl Denson")
            .setImportant(true)
            .build()

        val user = Person.Builder()
            .setName("You")
            .setImportant(true)
            .build()

        val message = NotificationCompat.MessagingStyle.Message(
            "Check this out",
            0L,
            sender
        ).setData(
            "image/",
            Uri.parse("android.resource://com.example.post30/" + R.drawable.big_floppa)
        )

        val target = Intent(context, BubbleActivity::class.java)
        target.putExtra(BubbleActivity.MESSAGE_TEXT, message.text)
        target.putExtra(BubbleActivity.MESSAGE_IMG_PATH, message.dataUri.toString())

        val category = "com.example.android.bubbles.category.TEXT_SHARE_TARGET"
        val bubbleIntent = PendingIntent.getActivity(
            context,
            0,
            target,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val shortcut = ShortcutInfoCompat.Builder(context, SHORTCUT_ID)
            .setLocusId(LocusIdCompat(SHORTCUT_ID))
            .setCategories(setOf(category))
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(sender.name!!)
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)

        val bubbleData = BubbleMetadata
            .Builder(
                bubbleIntent,
                IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground)
            )
            .setDesiredHeight(600)
            .build()

        val builder = NotificationCompat
            .Builder(context, Post30Application.CHAT_CHANNEL)
            .setBubbleMetadata(bubbleData)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addPerson(sender)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    REQUEST_CONTENT,
                    Intent(context, ConversationsActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOnlyAlertOnce(true)
            .setShortcutId(SHORTCUT_ID)
            .setLocusId(LocusIdCompat(SHORTCUT_ID))
            .setStyle(
                NotificationCompat
                    .MessagingStyle(user)
                    .addMessage(message)
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher_foreground,
                    "Reply",
                    PendingIntent.getBroadcast(
                        context,
                        ReplyReceiver.REQUEST_CONTENT,
                        Intent(context, ReplyReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
                .addRemoteInput(
                    RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
                        .setLabel("Enter reply")
                        .setChoices(arrayOf("Nice", "Very well", "LOL"))
                        .build()
                )
                .build()
            )

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(chatNotificationId, builder.build())
        }
    }

    companion object {
        var chatNotificationId = 0

        private const val REQUEST_CONTENT = 1
        private const val SHORTCUT_ID = "shortcut"
    }
}