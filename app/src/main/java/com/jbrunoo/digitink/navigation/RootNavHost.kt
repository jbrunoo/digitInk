package com.jbrunoo.digitink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jbrunoo.digitink.presentation.HomeScreen
import com.jbrunoo.digitink.presentation.PlayScreen
import com.jbrunoo.digitink.presentation.ResultScreen

@Composable
fun RootNavHost(navController: NavHostController) {
    NavHost(navController = navController,
        startDestination = Screen.HOME.route) {
        composable(Screen.HOME.route) {
            HomeScreen(navController)
        }
        composable(Screen.PLAY.route) {
            PlayScreen()
        }
        composable(Screen.RESULT.route) {
            ResultScreen()
        }
        composable(Screen.RANKING.route) {

        }
    }
}