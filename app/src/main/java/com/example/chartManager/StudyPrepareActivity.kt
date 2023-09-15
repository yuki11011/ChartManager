package com.example.chartManager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chartManager.ui.theme.Theme
import kotlin.math.floor

class StudyPrepareActivity : ComponentActivity() {
    private val viewModel: ChartViewModel by viewModels {
        ViewModelFactory((application as ChartApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getDataFromQuestionId(intent.getFloatExtra("QUESTION_NUMBER", 1f))

        setContent {
            Theme(
                darkTheme = false
            ) {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        Scaffold(
            topBar = { TopBar() },
        ) { padding ->
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
                            val questionNum = intent.getFloatExtra("QUESTION_NUMBER", 1f)
                            Text(
                                if (questionNum % floor(questionNum) == 0f) "${floor(questionNum).toInt()}(例題)" else "${floor(questionNum).toInt()}(練習)",
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
                            val solvingTime = viewModel.record.value.time
                            val minute = solvingTime / 60
                            val second = solvingTime % 60
                            Text(
                                if (viewModel.record.value.id != -1) "${minute}:${second}" else "–:–(記録なし)",
                                fontSize = 20.sp
                            )
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
            title = { Text(text = "チャート支援") }, // タイトルを指定
        )
    }
}