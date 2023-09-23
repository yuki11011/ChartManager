package com.example.chartManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class Notification(context: Context, intent:Intent?) {
    private val notificationManager: NotificationManager
    private val context = context
    private val intent = intent
    lateinit var notification: Notification

    companion object {
        const val CHANNEL_ID = "StudyRecordForegroundServiceChannel"
        const val CHANNEL_NAME = "学習記録"
        const val NOTIFICATION_ID = 1
    }

    init {
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            )
                .apply { setSound(null, null) }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(text: String) {
        val title = "学習記録"
        val text = "経過時間: $text"
        val icon = R.drawable.ic_launcher_foreground
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_pause, // ボタンのアイコン
            "一時停止", // ボタンのテキスト
            pendingIntent // ボタンをタップしたときに実行するPendingIntent
        ).build()
        builder.addAction(action)

        notification = builder.build()
    }

    fun notifyTime() {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}