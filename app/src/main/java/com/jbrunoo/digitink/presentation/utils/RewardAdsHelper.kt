package com.jbrunoo.digitink.presentation.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.jbrunoo.digitink.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class RewardAdsHelper @Inject constructor() {
    private var rewardedAd: RewardedAd? = null

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded = _isAdLoaded.asStateFlow()

    private fun loadRewardAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val adId = BuildConfig.REWARD_AD_ID

        RewardedAd.load(
            context,
            adId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    rewardedAd = p0
                    _isAdLoaded.value = true
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    rewardedAd = null
                    _isAdLoaded.value = false
                }
            },
        )
    }

    fun showRewardAd(
        activity: Activity,
        onRequireRewardItem: (Int) -> Unit,
    ) {
        if (rewardedAd == null) {
            Toast.makeText(activity, "Fail to load Ad", Toast.LENGTH_SHORT).show()
            return
        }

        rewardedAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Timber.d("Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Timber.d("Ad dismissed fullscreen content.")
                    rewardedAd = null
                    _isAdLoaded.value = false

                    loadRewardAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Timber.e("Ad failed to show fullscreen content.")
                    rewardedAd = null
                    _isAdLoaded.value = false
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Timber.d("Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Timber.d("Ad showed fullscreen content.")
                }
            }

        rewardedAd?.show(activity) { rewardItem ->
            onRequireRewardItem(rewardItem.amount)
        }
    }
}
