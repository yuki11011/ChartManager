package com.example.chartManager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.chartManager.ui.theme.Theme

class StudyPrepareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme (
                darkTheme = false
            ) {
                mainContent()
            }
        }
    }

    @Composable
    private fun mainContent() {
        val navController = rememberNavController()
        Scaffold(
            topBar = { TopBar() },
        ) {padding ->
            val intent = intent
            Column(
                modifier = Modifier.padding(padding)
            ) {
                Card(
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "問題番号:", fontSize = 20.sp)
                            Text(
                                intent.getIntExtra("QUESTION_NUMBER", 1).toString(),
                                fontSize = 20.sp
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "前回の終了時間:", fontSize = 20.sp)
                            Text(text = "13:23", fontSize = 20.sp)
                        }
                    }
                }
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .padding()
                        .padding(24.dp)
                        .fillMaxWidth(),
                ) {
                    Text("以上の問題の学習を始める")
                }
            }
        }
        }

    @Composable
    private fun TopBar() {
        TopAppBar(
            title = { androidx.compose.material.Text(text = "チャート支援") }, // タイトルを指定
        )
    }
}