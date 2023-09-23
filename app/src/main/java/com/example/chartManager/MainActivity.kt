package com.example.chartManager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Card
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chartManager.ui.theme.Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    private val viewModel: ChartViewModel by viewModels {
        ViewModelFactory((application as ChartApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getLatestId()
        viewModel.getHistory(10)
        viewModel.getReview(1, 5)

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
        val navController = rememberNavController()
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar(navController) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "study",
                modifier = Modifier.padding(padding)
            ) {
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
                    intent.putExtra("QUESTION_NUMBER", viewModel.latestId.value)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding()
                    .padding(24.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    "${floor(viewModel.latestId.value).toInt()}番の" +
                            if (viewModel.latestId.value % floor(viewModel.latestId.value) == 0f) "例題" else "練習" +
                                    "から学習を始める"
                )
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
                label = { Text("問題番号を入力(練習問題は.1)") },
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
                    intent.putExtra("QUESTION_NUMBER", questionNumber.value.toFloat())
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
        val review = viewModel.reviewList
        Column {
            Text("復習タスク")
            Divider()
            LazyColumn {
                itemsIndexed(review) { index, item ->
                    var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                    var date = LocalDateTime.parse(item.date.toString(), formatter)
                    formatter = DateTimeFormatter.ofPattern("yyyy年M月d日H:mm")
                    Row(
                        modifier = Modifier
                            .clickable {
                                val intent =
                                    Intent(this@MainActivity, StudyPrepareActivity::class.java)
                                intent.putExtra("QUESTION_NUMBER", item.question_id)
                                startActivity(intent)
                            }
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        val id = item.question_id
                        Text(
                            text = floor(id).toInt()
                                .toString() + if (id % floor(id) == 0f) "(例題)" else "(練習)",
                            color = Color(0xFF4C425A),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(
                                    2.dp,
                                    Color(0xFF4C425A),
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(5.dp),
                        )
                        Column {
                            Text(
                                text = item.time.toString() + "分",
                                color = Color(0xFFEF5B9C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Row {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "time",
                                    tint = Color(0xFF758492)
                                )
                                Text(
                                    text = date.format(formatter).toString(),
                                    color = Color(0xFF758492),
                                )
                            }
                        }
                    }
                    if (index < review.lastIndex) Divider()
                }
            }
        }
    }

    @Composable
    private fun Record() {
        val history = viewModel.history
        Column {
            Text("履歴")
            Text(text = "直近の10件を表示しています。", color = MaterialTheme.colors.secondary)
            Card(
                backgroundColor = Color(0xFFEEEEEE),
                modifier = Modifier.padding(10.dp)
            ) {
                Column {
                    LazyColumn {
                        itemsIndexed(history) { index, item ->
                            var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                            var date = LocalDateTime.parse(item.date.toString(), formatter)
                            formatter = DateTimeFormatter.ofPattern("yyyy年M月d日H:mm")
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            ) {
                                val id = item.question_id
                                Text(
                                    text = floor(id).toInt()
                                        .toString() + if (id % floor(id) == 0f) "(例題)" else "(練習)",
                                    color = Color(0xFF4C425A),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .border(
                                            2.dp,
                                            Color(0xFF4C425A),
                                            RoundedCornerShape(2.dp)
                                        )
                                        .padding(5.dp),
                                )
                                Column {
                                    Text(
                                        text = item.time.toString() + "分",
                                        color = Color(0xFFEF5B9C),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Row {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "time",
                                            tint = Color(0xFF758492)
                                        )
                                        Text(
                                            text = date.format(formatter).toString(),
                                            color = Color(0xFF758492),
                                        )
                                    }
                                }
                            }
                            if (index < history.lastIndex) Divider()
                        }
                    }
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

    @Composable
    private fun BottomBar(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        BottomAppBar {
            BottomNavigation {
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

    private val questionNumber = mutableStateOf("")
}
