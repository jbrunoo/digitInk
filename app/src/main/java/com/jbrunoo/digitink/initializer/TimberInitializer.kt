package com.jbrunoo.digitink.initializer

import android.content.Context
import androidx.startup.Initializer
import com.jbrunoo.digitink.BuildConfig
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(
                object : Timber.DebugTree() {
                    override fun createStackElementTag(element: StackTraceElement): String = String.format(
                        "Class:%s: Line: %s, Method: %s",
                        super.createStackElementTag(element),
                        element.lineNumber,
                        element.methodName,
                    )
                },
            )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
