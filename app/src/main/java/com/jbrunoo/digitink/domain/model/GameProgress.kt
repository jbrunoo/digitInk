package com.jbrunoo.digitink.domain.model

import com.jbrunoo.digitink.common.Constants

data class GameProgress(
    val coinCount: Int = 0,
    val infiniteMaxLifeCount: Int = Constants.DEFAULT_INFINITE_LIFE_COUNT,
) {
    val nextInfiniteLifeUpgradeCost: Int? =
        Constants.INFINITE_LIFE_UPGRADE_COSTS.getOrNull(
            infiniteMaxLifeCount - Constants.DEFAULT_INFINITE_LIFE_COUNT,
        )

    val canUpgradeInfiniteLife: Boolean =
        nextInfiniteLifeUpgradeCost?.let { coinCount >= it } ?: false
}
