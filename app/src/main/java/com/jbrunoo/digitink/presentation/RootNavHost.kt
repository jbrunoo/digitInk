package com.jbrunoo.digitink.presentation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.jbrunoo.digitink.presentation.home.HomeViewModel
import com.jbrunoo.digitink.presentation.home.view.HomeScreen
import com.jbrunoo.digitink.presentation.play.infinite.InfinitePlayScreen
import com.jbrunoo.digitink.presentation.play.normal.NormalPlayScreen
import com.jbrunoo.digitink.presentation.result.ResultScreen
import com.jbrunoo.digitink.presentation.utils.submitScoreToLeaderboard

@Composable
fun RootNavHost(
    navController: NavHostController,
    gamesSignInClient: GamesSignInClient,
    leaderboardsClient: LeaderboardsClient,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.HOME.route,
    ) {
        composable(Screen.HOME.route) {
            val context = LocalContext.current
            val activity = context as? Activity
            val viewModel = hiltViewModel<HomeViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.loadRewardAd(context)
            }

            HomeScreen(
                currentTicketCount = uiState.value.ticketCount,
                isRewardAdLoaded = uiState.value.isRewardAdLoaded,
                onPlayNormal = {
                    viewModel.startNormalPlay {
                        navController.navigate(Screen.PLAY.NORMAL.route + "/$it")
                    }
                },
                onPlayInfinite = {
                    viewModel.startInfinitePlay {
                        navController.navigate(Screen.PLAY.INFINITE.route)
                    }
                },
                onClickAd = {
                    activity?.let(viewModel::showRewardAd)
                },
                onClickResult = { navController.navigate(Screen.RESULT.route) },
            )
        }

        composable(
            Screen.PLAY.NORMAL.route + "/{questionCount}",
            arguments = listOf(navArgument("questionCount") { type = NavType.IntType }),
        ) {
            NormalPlayScreen(
                onTerminate = { navController.navigateWithPopUp(Screen.RESULT.route) },
                onSubmitScore = { leaderBoardKey, score ->
                    submitScoreToLeaderboard(
                        gamesSignInClient = gamesSignInClient,
                        leaderboardsClient = leaderboardsClient,
                        leaderBoardKey = leaderBoardKey,
                        score = score,
                    )
                },
                viewModel = hiltViewModel(),
            )
        }

        composable(
            Screen.PLAY.INFINITE.route,
        ) {
            InfinitePlayScreen(
                onTerminate = { navController.navigateWithPopUp(Screen.RESULT.route) },
                onSubmitScore = { leaderBoardKey, score ->
                    submitScoreToLeaderboard(
                        gamesSignInClient = gamesSignInClient,
                        leaderboardsClient = leaderboardsClient,
                        leaderBoardKey = leaderBoardKey,
                        score = score,
                    )
                },
                viewModel = hiltViewModel(),
            )
        }

        composable(Screen.RESULT.route) {
            ResultScreen(
                navigateToHome = { navController.navigateWithPopUp(Screen.HOME.route) },
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
