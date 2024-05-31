package com.jbrunoo.digitink.presentation

sealed class Screen(val route: String) {
    data object HOME: Screen("home")
    data object PLAY: Screen("play")
    data object RESULT: Screen("result")
}