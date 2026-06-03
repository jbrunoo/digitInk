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
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.jbrunoo.digitink.presentation.home.HomeViewModel
import com.jbrunoo.digitink.presentation.home.view.HomeScreen
import com.jbrunoo.digitink.presentation.play.infinite.InfinitePlayScreen
import com.jbrunoo.digitink.presentation.play.normal.NormalPlayScreen
import com.jbrunoo.digitink.presentation.result.ResultScreen
import com.jbrunoo.digitink.presentation.utils.showAchievements
import com.jbrunoo.digitink.presentation.utils.showLeaderboards
import com.jbrunoo.digitink.presentation.utils.submitLocalScoresToLeaderboards
import com.jbrunoo.digitink.presentation.utils.submitScoreToLeaderboard
import com.jbrunoo.digitink.presentation.utils.unlockInfiniteModeAchievements
import com.jbrunoo.digitink.presentation.utils.unlockNormalModeAchievement

@Composable
fun RootNavHost(
    navController: NavHostController,
    gamesSignInClient: GamesSignInClient,
    leaderboardsClient: LeaderboardsClient,
    achievementsClient: AchievementsClient,
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
                millisUntilNextTicket = uiState.value.millisUntilNextTicket,
                canWatchRewardAd = uiState.value.canWatchRewardAd,
                coinCount = uiState.value.coinCount,
                infiniteMaxLifeCount = uiState.value.infiniteMaxLifeCount,
                nextInfiniteLifeUpgradeCost = uiState.value.nextInfiniteLifeUpgradeCost,
                canUpgradeInfiniteLife = uiState.value.canUpgradeInfiniteLife,
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
                onPurchaseInfiniteLife = viewModel::purchaseInfiniteLifeUpgrade,
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
                onUnlockAchievement = { questionCount, correctCount ->
                    unlockNormalModeAchievement(
                        gamesSignInClient = gamesSignInClient,
                        achievementsClient = achievementsClient,
                        questionCount = questionCount,
                        correctCount = correctCount,
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
                onUnlockAchievements = { correctCount, playCount ->
                    unlockInfiniteModeAchievements(
                        gamesSignInClient = gamesSignInClient,
                        achievementsClient = achievementsClient,
                        correctCount = correctCount,
                        playCount = playCount,
                    )
                },
                viewModel = hiltViewModel(),
            )
        }

        composable(Screen.RESULT.route) {
            val context = LocalContext.current
            val activity = context as? Activity

            ResultScreen(
                navigateToHome = { navController.navigateWithPopUp(Screen.HOME.route) },
                onShowLeaderBoard = { score ->
                    showLeaderboards(
                        gamesSignInClient = gamesSignInClient,
                        leaderboardsClient = leaderboardsClient,
                        beforeShow = {
                            submitLocalScoresToLeaderboards(
                                leaderboardsClient = leaderboardsClient,
                                score = score,
                            )
                        },
                    ) { intent ->
                        activity?.startActivityForResult(intent, RC_LEADERBOARD_UI)
                    }
                },
                onShowAchievements = {
                    showAchievements(
                        gamesSignInClient = gamesSignInClient,
                        achievementsClient = achievementsClient,
                    ) { intent ->
                        activity?.startActivityForResult(intent, RC_ACHIEVEMENT_UI)
                    }
                },
            )
        }
    }
}

private const val RC_LEADERBOARD_UI = 9004
private const val RC_ACHIEVEMENT_UI = 9005

fun NavHostController.navigateWithPopUp(route: String) {
    this.navigate(route) {
        popUpTo(Screen.HOME.route)
        launchSingleTop = true
    }
}
