package com.jbrunoo.digitink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jbrunoo.digitink.presentation.HomeScreen
import com.jbrunoo.digitink.presentation.play.PlayScreen
import com.jbrunoo.digitink.presentation.ResultScreen

@Composable
fun RootNavHost(navController: NavHostController) {
    NavHost(navController = navController,
        startDestination = Screen.HOME.route) {
        composable(Screen.HOME.route) {
            HomeScreen(navController)
        }
        composable(Screen.PLAY.route + "/{questionCount}",
            arguments = listOf(navArgument("questionCount") { type = NavType.IntType })
        ) {
            PlayScreen(navController)
        }
        composable(Screen.RESULT.route) {
            ResultScreen()
        }
        composable(Screen.RANKING.route) {

        }
    }
}