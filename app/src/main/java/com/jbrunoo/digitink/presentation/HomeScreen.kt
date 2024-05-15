package com.jbrunoo.digitink.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.jbrunoo.digitink.navigation.Screen

@Composable
fun HomeScreen(navHostController: NavHostController) {
    Column {
        Button(onClick = { navHostController.navigate(Screen.PLAY.route) }) {
            Text("플레이")
        }
        Button(onClick = { navHostController.navigate(Screen.RANKING.route) }) {
            Text("리더보드")
        }
    }
}