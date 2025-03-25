package com.jbrunoo.digitink.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.android.gms.games.PlayGamesSdk

class PlayGamesInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        PlayGamesSdk.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}