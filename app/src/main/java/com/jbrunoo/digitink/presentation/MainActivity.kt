package com.jbrunoo.digitink.presentation

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.navigation.compose.rememberNavController
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.designsystem.component.BannerAd
import com.jbrunoo.digitink.designsystem.theme.DigitInkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        enableEdgeToEdge()

        initializeTicket()

        setContent {
            DigitInkTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        topBar = {
                            BannerAd(
                                modifier = Modifier.statusBarsPadding(),
                            )
                        },
                        contentWindowInsets = WindowInsets.safeDrawing,
                    ) { innerPadding ->
                        RootNavHost(
                            navController = rememberNavController(),
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }

    private fun initializeTicket() {
        val scope = CoroutineScope(Dispatchers.IO)
        val key = intPreferencesKey(Constants.TICKET_KEY)

        scope.launch {
            dataStore.edit { preferences ->
                if (!preferences.contains(key)) {
                    val maxTicket = 3
                    preferences[key] = maxTicket
                }
            }
        }
    }
}
