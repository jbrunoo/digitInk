package com.jbrunoo.digitink.domain.model

data class Result(
    val speedGame5: Int = 0,
    val speedGame10: Int = 0,
    val speedGame15: Int = 0,
    val speedGame20: Int = 0,
) {
    fun find(count: Int): Int {
        return when(count) {
            5 -> speedGame5
            10 -> speedGame10
            15 -> speedGame15
            else -> speedGame20
        }
    }
}