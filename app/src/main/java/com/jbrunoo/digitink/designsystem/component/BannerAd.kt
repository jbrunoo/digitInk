package com.jbrunoo.digitink.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.jbrunoo.digitink.BuildConfig

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BuildConfig.BANNER_AD_ID
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        },
    )
}
