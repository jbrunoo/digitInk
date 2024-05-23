package com.jbrunoo.digitink.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jbrunoo.digitink.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {
    var isExpanded by remember { mutableStateOf(false) }
    val questionCounts = listOf(5, 10, 15, 20)
    var selectedQuestionCount by remember { mutableIntStateOf(5) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.wrapContentSize()) {
                Card(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Text(text = selectedQuestionCount.toString())
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = !isExpanded }
                ) {
                    questionCounts.forEach {
                        DropdownMenuItem(text = { it.toString() },
                            onClick = {
                                selectedQuestionCount = it
                            }
                        )
                    }
                }
            }
            Button(
                onClick = { navHostController.navigate(Screen.PLAY.route + "/${selectedQuestionCount}") }
            ) {
                Text("플레이")
            }
        }
        Button(onClick = { navHostController.navigate(Screen.RANKING.route) }) {
            Text("리더보드")
        }
    }


}