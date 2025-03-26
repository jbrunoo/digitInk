package com.jbrunoo.digitink.presentation.play

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface PlayUIState {
    data object LOADING: PlayUIState
    data class SUCCESS(
        val qnaList: List<QnaState>,
        val pathsList: List<List<PathState>>
    ): PlayUIState
}

data class QnaState(
    val question: String,
    val answer: Int,
    val checkDrawResult: Boolean? = null
)

data class PathState(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.White,
    val alpha: Float = 0.8f,
    val strokeWidth: Dp = 4.dp
)
