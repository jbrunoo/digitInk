package com.jbrunoo.digitink.presentation

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jbrunoo.digitink.playgames.PlayGamesManager
import com.jbrunoo.digitink.presentation.component.BannerAd
import com.jbrunoo.digitink.ui.theme.DigitInkTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playGamesManager: PlayGamesManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        enableEdgeToEdge()

        playGamesManager.initialize(this@MainActivity)

        setContent {
            DigitInkTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { BannerAd(
                            modifier = Modifier.statusBarsPadding(),
                        ) },
                        contentWindowInsets = WindowInsets.safeDrawing,
                    ) { innerPadding ->
                        RootNavHost(
                            navController = rememberNavController(),
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
