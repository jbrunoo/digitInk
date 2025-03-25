package com.jbrunoo.digitink.domain.model

data class Result(
    val speedGame5: Long = 0,
    val speedGame10: Long = 0,
    val speedGame15: Long = 0,
    val speedGame20: Long = 0,
) {
    fun find(count: Int): Long {
        return when(count) {
            5 -> speedGame5
            10 -> speedGame10
            15 -> speedGame15
            else -> speedGame20
        }
    }
}