package com.example.chartManager

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chartManager.ui.theme.Theme
import kotlin.math.floor


class StudyActivity : ComponentActivity() {
    lateinit var mService: StudyRecordForegroundService
    var mBound: Boolean = false
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as StudyRecordForegroundService.StudyRecordBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }
    private val viewModel: ChartViewModel by viewModels {
        ViewModelFactory((application as ChartApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this@StudyActivity, StudyRecordForegroundService::class.java)
        startForegroundService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        broadcastReceiver = StudyBroadCastReceiver()
        registerReceiver(broadcastReceiver, IntentFilter("com.example.chartManager.GET_TIME_COUNT"))
        setContent {
            Theme(
                darkTheme = false
            ) {
                MainContent()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        val intent = Intent(this@StudyActivity, StudyRecordForegroundService::class.java)
        stopService(intent)
    }

    inner class StudyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("com.example.chartManager.GET_TIME_COUNT")) {
                viewModel.timeCount.value = intent.getStringExtra("TIME_COUNT").toString()
            }
        }
    }

    @Composable
    private fun MainContent() {
        Scaffold(
            topBar = { TopBar() }
        ) { padding ->
            val isStarted = remember { mutableStateOf(false) }
            val questionId = intent.getFloatExtra("QUESTION_NUMBER", 1f)

            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                Text(
                    if (questionId % floor(questionId) == 0f) "${floor(questionId).toInt()}(例題)" else "${floor(questionId).toInt()}(練習)",
                    fontSize = 20.sp
                    )
                Text(
                    viewModel.timeCount.value,
                    fontSize = 70.sp
                )
                Button(
                    onClick = {
                        isStarted.value = !isStarted.value
                        mService.changeTimerState()
                    },
                    modifier = Modifier
                        .padding()
                        .padding(24.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        if (!isStarted.value) {
                            "スタート"
                        } else {
                            "一時停止"
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun TopBar() {
        TopAppBar(
            title = { Text(text = "チャート支援") }, // タイトルを指定
        )
    }

}
