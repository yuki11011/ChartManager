package com.example.chartManager

import android.os.Handler
import android.os.Looper
import android.util.Log

class Timer: Runnable {
    private var elapsedTime = 0L
    private var formattedTime = "00:00"
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var task: () -> Unit

    init {
        handler.postDelayed(this, 1000)
    }

    fun setTask(task: () -> Unit) {
        this.task = task
    }

    fun changeTimerState() {
        isRunning = !isRunning
    }

    fun getCurrentTime(): String {
        return this.formattedTime
    }


    override fun run() {
        if (isRunning) {
            elapsedTime++

            val minutes = elapsedTime / 60
            val seconds = elapsedTime % 60
            formattedTime = String.format("%02d:%02d", minutes, seconds)

            task()

            Log.v("info", "runnable executed")
        }
        handler.postDelayed(this, 1000)
    }
}