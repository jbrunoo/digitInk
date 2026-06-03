package com.jbrunoo.digitink.presentation.home

data class HomeUIState(
    val ticketCount: Int = 0,
    val isRewardAdLoaded: Boolean = false,
    val millisUntilNextTicket: Long = 0L,
    val canWatchRewardAd: Boolean = false,
    val coinCount: Int = 0,
    val infiniteMaxLifeCount: Int = 5,
    val nextInfiniteLifeUpgradeCost: Int? = null,
    val canUpgradeInfiniteLife: Boolean = false,
)
