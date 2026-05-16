package com.jbrunoo.digitink.presentation.utils

import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
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
                gamesSignInClient.signIn()
                    .addOnSuccessListener { signInResult ->
                        if (signInResult.isAuthenticated) {
                            submit()
                        } else {
                            Timber.w("Play Games sign-in is required to submit leaderboard score.")
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
