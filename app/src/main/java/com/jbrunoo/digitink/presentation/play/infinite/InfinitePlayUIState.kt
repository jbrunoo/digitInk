package com.jbrunoo.digitink.presentation.play.infinite

import com.jbrunoo.digitink.presentation.play.domain.model.QnaWithPath

sealed interface InfinitePlayUIState {
    data object LOADING: InfinitePlayUIState
    data class SUCCESS(
        val lifeCount : Int = 5,
        val qnaWithPathList: List<QnaWithPath>,
    ): InfinitePlayUIState
}
