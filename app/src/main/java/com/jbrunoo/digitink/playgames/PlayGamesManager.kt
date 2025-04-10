package com.jbrunoo.digitink.playgames

import android.app.Activity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class PlayGamesManager {
    private lateinit var gamesSignInClient: GamesSignInClient
    private lateinit var leaderboardsClient: LeaderboardsClient
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun initialize(activity: Activity) {
        gamesSignInClient = PlayGames.getGamesSignInClient(activity)
        leaderboardsClient = PlayGames.getLeaderboardsClient(activity)

        checkAuth()
    }

    fun checkAuth() {
        gamesSignInClient.run {
            isAuthenticated().addOnCompleteListener { isAuthenticatedTask: Task<AuthenticationResult> ->
                val authResult =
                    (isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated)

                _isAuthenticated.value = authResult

                if (authResult) {
                    Timber.d("complete authenticated")
                } else {
                    Timber.d("not authenticated - need signIn")
                    signIn()
                }
            }
        }
    }

    fun showLeaderBoard(activity: Activity) {
        checkAuth()

        if (!_isAuthenticated.value) {
            Timber.d("showLeaderBoard - not Authenticated")
            return
        }

        leaderboardsClient.allLeaderboardsIntent.addOnSuccessListener { intent ->
            startActivityForResult(
                activity,
                intent,
                RC_LEADERBOARD_UI,
                null
            )
        }

//        leaderboardsClient.getLeaderboardIntent(leaderBoardKey)
//            .addOnSuccessListener { intent ->
//                startActivityForResult(
//                    activity,
//                    intent,
//                    RC_LEADERBOARD_UI,
//                    null
//                )
//            }
    }

    fun submitScore(leaderBoardKey: String, score: Long) {
        if (!_isAuthenticated.value) {
            Timber.d("submitScore - not Authenticated")
            return
        }

        leaderboardsClient.submitScore(leaderBoardKey, score)
    }

    companion object {
        private const val RC_LEADERBOARD_UI = 9004
    }
}