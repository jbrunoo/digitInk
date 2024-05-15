package com.jbrunoo.digitink.navigation

sealed class Screen(val route: String) {
    data object HOME: Screen("home")
    data object PLAY: Screen("play")
    data object RESULT: Screen("result")
    data object RANKING: Screen("ranking")
}