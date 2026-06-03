package com.jbrunoo.digitink.presentation

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.designsystem.component.BannerAd
import com.jbrunoo.digitink.designsystem.theme.DigitInkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    private var isUpdateReadyToInstall by mutableStateOf(false)

    private val appUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.w("Flexible in-app update flow ended: resultCode=${result.resultCode}")
            }
        }

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @Inject
    lateinit var leaderboardsClient: LeaderboardsClient

    @Inject
    lateinit var gamesSignInClient: GamesSignInClient

    @Inject
    lateinit var achievementsClient: AchievementsClient

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        enableEdgeToEdge()

        initializeInAppUpdate()
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
                            gamesSignInClient = gamesSignInClient,
                            leaderboardsClient = leaderboardsClient,
                            achievementsClient = achievementsClient,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }

                    if (isUpdateReadyToInstall) {
                        AlertDialog(
                            onDismissRequest = { isUpdateReadyToInstall = false },
                            title = {
                                Text(text = stringResource(R.string.in_app_update_ready_title))
                            },
                            text = {
                                Text(text = stringResource(R.string.in_app_update_ready_message))
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        appUpdateManager.completeUpdate()
                                    },
                                ) {
                                    Text(text = stringResource(R.string.in_app_update_restart_button))
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { isUpdateReadyToInstall = false },
                                ) {
                                    Text(text = stringResource(R.string.in_app_update_later_button))
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::appUpdateManager.isInitialized) {
            appUpdateManager.appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        isUpdateReadyToInstall = true
                    }
                }
                .addOnFailureListener {
                    Timber.w(it, "Failed to check in-app update install status.")
                }
        }
    }

    override fun onDestroy() {
        installStateUpdatedListener?.let { appUpdateManager.unregisterListener(it) }
        super.onDestroy()
    }

    private fun initializeInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        installStateUpdatedListener =
            InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    isUpdateReadyToInstall = true
                }
            }.also(appUpdateManager::registerListener)

        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                val isFlexibleUpdateAvailable =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

                if (isFlexibleUpdateAvailable) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                    )
                }
            }
            .addOnFailureListener {
                Timber.w(it, "Failed to check flexible in-app update availability.")
            }
    }

    private fun initializeTicket() {
        val scope = CoroutineScope(Dispatchers.IO)
        val ticketKey = intPreferencesKey(Constants.TICKET_KEY)
        val refillAtKey = longPreferencesKey(Constants.TICKET_REFILL_AT_KEY)

        scope.launch {
            dataStore.edit { preferences ->
                if (!preferences.contains(ticketKey)) {
                    preferences[ticketKey] = Constants.MAX_TICKET_COUNT
                }
                if (!preferences.contains(refillAtKey)) {
                    preferences[refillAtKey] = 0L
                }
            }
        }
    }
}
