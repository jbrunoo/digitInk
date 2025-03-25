package com.jbrunoo.digitink.utils

// datastore key by question count
fun Int.datastoreKey(): String? {
    return when (this) {
        5 -> Constants.DATASTORE_KEY_5
        10 -> Constants.DATASTORE_KEY_10
        15 -> Constants.DATASTORE_KEY_15
        20 -> Constants.DATASTORE_KEY_20
        else -> null
    }
}

// leaderboard id by question count
fun Int.leaderBoardKey(): String? {
    return when (this) {
        5 -> Constants.LEADERBOARD_KEY_5
        10 -> Constants.LEADERBOARD_KEY_10
        15 -> Constants.LEADERBOARD_KEY_15
        20 -> Constants.LEADERBOARD_KEY_20
        else -> null
    }
}