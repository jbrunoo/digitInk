package com.jbrunoo.digitink.presentation.utils

import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.games.LeaderboardsClient
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class LeaderBoardsManager @Inject constructor(
    private val leaderboardsClient: LeaderboardsClient,
) {
    fun showLeaderBoard(context: Context) {
        leaderboardsClient.allLeaderboardsIntent.addOnSuccessListener { intent ->
            startActivityForResult(
                context as Activity,
                intent,
                RC_LEADERBOARD_UI,
                null,
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

    companion object {
        private const val RC_LEADERBOARD_UI = 9004
    }
}
