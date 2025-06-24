package com.jbrunoo.digitink.data.dataSource.remote

import kotlinx.coroutines.flow.StateFlow

interface AuthRemoteDataSource {
    val isAuthenticated: StateFlow<Boolean>

    fun checkAuth()
}
