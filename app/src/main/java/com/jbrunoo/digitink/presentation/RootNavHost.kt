package com.jbrunoo.digitink.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jbrunoo.digitink.presentation.home.view.HomeScreen
import com.jbrunoo.digitink.presentation.play.infinite.InfinitePlayScreen
import com.jbrunoo.digitink.presentation.play.normal.NormalPlayScreen
import com.jbrunoo.digitink.presentation.result.ResultScreen

@Composable
fun RootNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.HOME.route
    ) {
        composable(Screen.HOME.route) {
            HomeScreen(
                onPlayNormal = { navController.navigate(Screen.PLAY.NORMAL.route + "/$it") },
                onPlayInfinite = { navController.navigate(Screen.PLAY.INFINITE.route) },
                onClickResult = { navController.navigate(Screen.RESULT.route) }
            )
        }

        composable(
            Screen.PLAY.NORMAL.route + "/{questionCount}",
            arguments = listOf(navArgument("questionCount") { type = NavType.IntType })
        ) {
            NormalPlayScreen(
                onTerminate = { navController.navigateWithPopUp(Screen.RESULT.route) })
        }

        composable(
            Screen.PLAY.INFINITE.route,
        ) {
            InfinitePlayScreen(
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
