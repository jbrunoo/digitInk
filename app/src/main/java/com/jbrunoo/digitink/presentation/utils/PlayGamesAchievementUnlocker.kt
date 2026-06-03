package com.jbrunoo.digitink.presentation.utils

import android.content.Intent
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.GamesSignInClient
import com.jbrunoo.digitink.common.Constants
import timber.log.Timber

fun unlockNormalModeAchievement(
    gamesSignInClient: GamesSignInClient,
    achievementsClient: AchievementsClient,
    questionCount: Int,
    correctCount: Int,
) {
    if (correctCount != questionCount) return

    val achievementId = when (questionCount) {
        5 -> Constants.ACHIEVEMENT_COMPLETE_5
        10 -> Constants.ACHIEVEMENT_COMPLETE_10
        15 -> Constants.ACHIEVEMENT_COMPLETE_15
        20 -> Constants.ACHIEVEMENT_COMPLETE_20
        else -> return
    }

    unlockAchievement(
        gamesSignInClient = gamesSignInClient,
        achievementsClient = achievementsClient,
        achievementId = achievementId,
    )
}

fun unlockInfiniteModeAchievements(
    gamesSignInClient: GamesSignInClient,
    achievementsClient: AchievementsClient,
    correctCount: Int,
    playCount: Int,
) {
    listOf(
        50 to Constants.ACHIEVEMENT_INFINITE_CHALLENGE_50,
        100 to Constants.ACHIEVEMENT_INFINITE_CHALLENGE_100,
        150 to Constants.ACHIEVEMENT_INFINITE_CHALLENGE_150,
        200 to Constants.ACHIEVEMENT_INFINITE_CHALLENGE_200,
    ).forEach { (targetCount, achievementId) ->
        if (correctCount >= targetCount) {
            unlockAchievement(
                gamesSignInClient = gamesSignInClient,
                achievementsClient = achievementsClient,
                achievementId = achievementId,
            )
        }
    }

    listOf(
        Constants.ACHIEVEMENT_INFINITE_10_TIMES,
        Constants.ACHIEVEMENT_INFINITE_100_TIMES,
    ).forEach { achievementId ->
        setAchievementSteps(
            gamesSignInClient = gamesSignInClient,
            achievementsClient = achievementsClient,
            achievementId = achievementId,
            steps = playCount,
        )
    }
}

fun showAchievements(
    gamesSignInClient: GamesSignInClient,
    achievementsClient: AchievementsClient,
    onShow: (Intent) -> Unit,
) {
    fun show() {
        achievementsClient.achievementsIntent
            .addOnSuccessListener(onShow)
            .addOnFailureListener {
                Timber.w(it, "Failed to open Play Games achievements.")
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
                            Timber.w("Play Games sign-in is required to open achievements.")
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

private fun unlockAchievement(
    gamesSignInClient: GamesSignInClient,
    achievementsClient: AchievementsClient,
    achievementId: String,
) {
    gamesSignInClient.isAuthenticated
        .addOnSuccessListener { authenticationResult ->
            if (authenticationResult.isAuthenticated) {
                achievementsClient.unlock(achievementId)
                Timber.d("Achievement unlocked: achievement=$achievementId")
            } else {
                Timber.w("Skip achievement unlock because Play Games is not authenticated.")
            }
        }
        .addOnFailureListener {
            Timber.w(it, "Failed to check Play Games authentication for achievement unlock.")
        }
}

private fun setAchievementSteps(
    gamesSignInClient: GamesSignInClient,
    achievementsClient: AchievementsClient,
    achievementId: String,
    steps: Int,
) {
    gamesSignInClient.isAuthenticated
        .addOnSuccessListener { authenticationResult ->
            if (authenticationResult.isAuthenticated) {
                achievementsClient.setSteps(achievementId, steps)
                Timber.d("Achievement steps updated: achievement=$achievementId steps=$steps")
            } else {
                Timber.w("Skip achievement step update because Play Games is not authenticated.")
            }
        }
        .addOnFailureListener {
            Timber.w(it, "Failed to check Play Games authentication for achievement step update.")
        }
}
