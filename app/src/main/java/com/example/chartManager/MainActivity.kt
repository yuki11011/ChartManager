package com.example.chartManager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import com.example.chartManager.ui.theme.Theme

class MainActivity : ComponentActivity() {
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
            bottomBar = { BottomBar(navController) }
        ) {padding->
            NavHost(navController = navController, startDestination = "study", modifier = Modifier.padding(padding)) {
                composable("study") { Study() }
                composable("review") { Review() }
                composable("record") { Record() }
            }
        }
    }

    @Composable
    private fun Study() {
        Column {
            val state = remember { mutableStateOf(false) }
            Text("学習を開始する")
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, StudyPrepareActivity::class.java)
                    intent.putExtra("QUESTION_NUMBER", questionNumber.value.toInt())
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding()
                    .padding(24.dp)
                    .fillMaxWidth(),
                ) {
                Text("${questionNumber.value}番から学習を始める")
            }
            Divider()
            Text("または以下のページから学習を始める")
            OutlinedTextField(
                value = questionNumber.value,
                onValueChange = {
                    questionNumber.value = it
                    state.value = questionNumber.value.isNotEmpty()
                                },
                singleLine = true,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                label = {Text("問題番号を入力(練習問題は+1)")},
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.secondary,
                    focusedLabelColor = MaterialTheme.colors.secondary,
                    textColor = MaterialTheme.colors.secondary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, StudyPrepareActivity::class.java)
                    intent.putExtra("QUESTION_NUMBER", questionNumber.value.toInt())
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding()
                    .padding(24.dp)
                    .fillMaxWidth(),
                enabled = state.value
                ) {
                Text("学習を始める")
            }
        }
    }

    @Composable
    private fun Review() {
        Text("復習タスク")
        Divider()
    }

    @Composable
    private fun Record() {}

    @Composable
    private fun TopBar() {
        TopAppBar(
            title = { Text(text = "チャート支援") }, // タイトルを指定
        )
    }

    @Composable
    private fun BottomBar(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        BottomAppBar(
        ) {
            BottomNavigation() {
                BottomNavigationItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "study" } == true,
                    onClick = { navController.navigate("study") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("学習開始") }
                )
                BottomNavigationItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "review" } == true,
                    onClick = { navController.navigate("review") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("復習") }
                )
                BottomNavigationItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "record" } == true,
                    onClick = { navController.navigate("record") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AutoGraph,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("記録") }
                )
            }
        }
    }

    private val questionNumber = mutableStateOf("1")

}
