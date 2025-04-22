package com.jbrunoo.digitink.initializer

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber
import com.jbrunoo.digitink.BuildConfig

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(
                object : Timber.DebugTree() {
                    override fun createStackElementTag(element: StackTraceElement): String {
                        return String.format(
                            "Class:%s: Line: %s, Method: %s",
                            super.createStackElementTag(element),
                            element.lineNumber,
                            element.methodName
                        )
                    }
                }
            )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
