package com.jbrunoo.digitink.presentation

sealed class Screen(val route: String) {
    data object HOME: Screen("home")

    sealed class PLAY(route: String) : Screen(route) {
        data object INFINITE : PLAY("infinite-play")
        data object NORMAL : PLAY("normal-play")
    }

    data object RESULT: Screen("result")
}
