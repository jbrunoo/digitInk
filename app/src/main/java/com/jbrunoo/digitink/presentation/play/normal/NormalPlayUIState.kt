package com.jbrunoo.digitink.presentation.play.normal

import com.jbrunoo.digitink.domain.model.QnaWithPath

sealed interface NormalPlayUIState {
    data object LOADING : NormalPlayUIState
    data class SUCCESS(
        val limitTime: Long,
        val qnaWithPathList: List<QnaWithPath>,
    ) : NormalPlayUIState
}
