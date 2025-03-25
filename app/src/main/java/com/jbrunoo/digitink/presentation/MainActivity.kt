package com.jbrunoo.digitink.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.games.PlayGamesSdk
import com.jbrunoo.digitink.playgames.PlayGamesManager
import com.jbrunoo.digitink.ui.theme.DigitInkTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playGamesManager: PlayGamesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playGamesManager.apply {
            initialize(this@MainActivity)
            checkAuth()
        }

        setContent {
            DigitInkTheme {
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