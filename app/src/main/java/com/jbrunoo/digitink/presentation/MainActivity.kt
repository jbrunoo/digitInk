package com.jbrunoo.digitink.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.PlayGames
import com.google.android.gms.tasks.Task
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.ui.theme.DigitInkTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gamesSignInClient = PlayGames.getGamesSignInClient(this)
        fun showLeaderboard(leaderBoardId: Int) {
            PlayGames.getLeaderboardsClient(this)
                .getLeaderboardIntent(leaderBoardId.toString())
                .addOnSuccessListener { intent ->
                    ActivityCompat.startActivityForResult(
                        this,
                        intent,
                        RC_LEADERBOARD_UI,
                        null
                    )
                }
        }
        gamesSignInClient.isAuthenticated()
            .addOnCompleteListener { isAuthenticatedTask: Task<AuthenticationResult> ->
                val isAuthenticated =
                    (isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated)
                if (isAuthenticated) {
                    Timber.d("authenticated")
                    showLeaderboard(R.string.leaderboard_perfect_speedrun__5)
                } else {
                    Timber.d("not authenticatd - need signIn")
                    gamesSignInClient.signIn()
                }
            }


        setContent {
            DigitInkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavHost(navController = rememberNavController())
                }
            }
        }
    }
}
private const val RC_LEADERBOARD_UI = 9004
