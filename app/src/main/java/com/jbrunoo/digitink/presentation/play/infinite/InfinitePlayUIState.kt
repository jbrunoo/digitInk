package com.jbrunoo.digitink.presentation.play.infinite

import com.jbrunoo.digitink.presentation.play.normal.PathState
import com.jbrunoo.digitink.presentation.play.normal.QnaState

sealed interface InfinitePlayUIState {
    data object LOADING: InfinitePlayUIState
    data class SUCCESS(
        val lifeCount : Int = 5,
        val qnaList: List<QnaState>,
        val pathsList: List<List<PathState>>
    ): InfinitePlayUIState
}