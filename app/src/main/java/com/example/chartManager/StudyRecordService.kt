package com.example.chartManager

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class StudyRecordForegroundService : Service() {
    private val binder = StudyRecordBinder()
    private lateinit var notification: Notification
    private val timer = Timer()

    inner class StudyRecordBinder : Binder() {
        fun getService(): StudyRecordForegroundService {
            return this@StudyRecordForegroundService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification = Notification(this, intent)
        notification.createNotification("00:00")
        startForeground(1, notification.notification)

        timer.setTask {
            val formattedTime = timer.getCurrentTime()
            notification.createNotification(formattedTime)
            notification.notifyTime()

            val intent = Intent().apply {
                action = "com.example.chartManager.GET_TIME_COUNT"
                putExtra("TIME_COUNT", formattedTime)
            }
            sendBroadcast(intent)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun changeTimerState() {
        timer.changeTimerState()
    }
}
