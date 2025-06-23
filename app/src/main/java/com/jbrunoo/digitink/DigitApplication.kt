package com.jbrunoo.digitink

import android.app.Application
import com.jbrunoo.digitink.ml.MnistModel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DigitApplication : Application() {
    @Inject lateinit var model: MnistModel

    override fun onTerminate() {
        model.close()
        super.onTerminate()
    }
}
