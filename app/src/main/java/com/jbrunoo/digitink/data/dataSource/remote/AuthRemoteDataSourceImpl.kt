package com.jbrunoo.digitink.data.dataSource.remote

import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val gamesSignInClient: GamesSignInClient,
) : AuthRemoteDataSource {

    private val _isAuthenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val isAuthenticated: StateFlow<Boolean>
        get() = _isAuthenticated.asStateFlow()

    override fun checkAuth() {
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
}
