package com.jbrunoo.digitink.presentation.utils

import android.content.Intent
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.domain.model.Score
import timber.log.Timber

fun submitScoreToLeaderboard(
    gamesSignInClient: GamesSignInClient,
    leaderboardsClient: LeaderboardsClient,
    leaderBoardKey: String,
    score: Long,
) {
    fun submit() {
        leaderboardsClient.submitScoreImmediate(leaderBoardKey, score)
            .addOnSuccessListener {
                Timber.d("Leaderboard score submitted: leaderboard=$leaderBoardKey score=$score")
            }
            .addOnFailureListener {
                Timber.w(it, "Failed to submit leaderboard score: leaderboard=$leaderBoardKey")
            }
    }

    gamesSignInClient.isAuthenticated
        .addOnSuccessListener { authenticationResult ->
            if (authenticationResult.isAuthenticated) {
                submit()
            } else {
                Timber.w("Skip leaderboard score submit because Play Games is not authenticated.")
            }
        }
        .addOnFailureListener {
            Timber.w(it, "Failed to check Play Games sign-in state.")
        }
}

fun showLeaderboards(
    gamesSignInClient: GamesSignInClient,
    leaderboardsClient: LeaderboardsClient,
    beforeShow: () -> Unit = {},
    onShow: (Intent) -> Unit,
) {
    fun show() {
        beforeShow()
        leaderboardsClient.allLeaderboardsIntent
            .addOnSuccessListener(onShow)
            .addOnFailureListener {
                Timber.w(it, "Failed to open Play Games leaderboards.")
            }
    }

    gamesSignInClient.isAuthenticated
        .addOnSuccessListener { authenticationResult ->
            if (authenticationResult.isAuthenticated) {
                show()
            } else {
                gamesSignInClient.signIn()
                    .addOnSuccessListener { signInResult ->
                        if (signInResult.isAuthenticated) {
                            show()
                        } else {
                            Timber.w("Play Games sign-in is required to open leaderboards.")
                        }
                    }
                    .addOnFailureListener {
                        Timber.w(it, "Failed to sign in to Play Games.")
                    }
            }
        }
        .addOnFailureListener {
            Timber.w(it, "Failed to check Play Games sign-in state.")
        }
}

fun submitLocalScoresToLeaderboards(
    leaderboardsClient: LeaderboardsClient,
    score: Score,
) {
    mapOf(
        Constants.LEADERBOARD_KEY_5 to score.normalMode5,
        Constants.LEADERBOARD_KEY_10 to score.normalMode10,
        Constants.LEADERBOARD_KEY_15 to score.normalMode15,
        Constants.LEADERBOARD_KEY_20 to score.normalMode20,
        Constants.LEADERBOARD_KEY_INFINITE to score.infiniteMode,
    ).forEach { (leaderboardKey, value) ->
        if (value > 0L) {
            leaderboardsClient.submitScoreImmediate(leaderboardKey, value)
                .addOnFailureListener {
                    Timber.w(it, "Failed to sync local leaderboard score: leaderboard=$leaderboardKey")
                }
        }
    }
}
