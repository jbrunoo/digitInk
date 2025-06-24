package com.jbrunoo.digitink.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.android.gms.ads.MobileAds

class MobileAdsInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        MobileAds.initialize(context)

        // 내 기기 제외 - 프로그매틱 말고 admob 내에서 설정함
//        val testDeviceIds = listOf("")
//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
//
//        MobileAds.setRequestConfiguration(configuration)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
