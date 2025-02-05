package com.jbrunoo.digitink.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jbrunoo.digitink.presentation.home.HomeScreen
import com.jbrunoo.digitink.presentation.play.PlayScreen
import com.jbrunoo.digitink.presentation.result.ResultScreen

@Composable
fun RootNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.HOME.route
    ) {
        composable(Screen.HOME.route) {
            HomeScreen(
                onClickPlay = { navController.navigate(Screen.PLAY.route + "/$it") },
                onClickResult = { navController.navigate(Screen.RESULT.route) }
            )
        }

        composable(
            Screen.PLAY.route + "/{questionCount}",
            arguments = listOf(navArgument("questionCount") { type = NavType.IntType })
        ) {
            PlayScreen(
                onTerminate = { navController.navigateWithPopUp(Screen.RESULT.route) })
        }

        composable(Screen.RESULT.route) {
            ResultScreen(
                navigateToHome = { navController.navigateWithPopUp(Screen.HOME.route) }
            )
        }
    }
}

fun NavHostController.navigateWithPopUp(route: String) {
    this.navigate(route) {
        popUpTo(Screen.HOME.route)
        launchSingleTop = true
    }
}