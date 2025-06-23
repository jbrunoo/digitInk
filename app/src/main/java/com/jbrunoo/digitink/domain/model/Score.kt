package com.jbrunoo.digitink.domain.model

data class Score(
    val normalMode5: Long = 0,
    val normalMode10: Long = 0,
    val normalMode15: Long = 0,
    val normalMode20: Long = 0,
    val infiniteMode: Long = 0,
) {
    fun find(count: Int): Double {
        val value =
            when (count) {
                5 -> normalMode5
                10 -> normalMode10
                15 -> normalMode15
                20 -> normalMode20
                else -> return infiniteMode.toDouble()
            }

        return value / 100.0
    }
}
