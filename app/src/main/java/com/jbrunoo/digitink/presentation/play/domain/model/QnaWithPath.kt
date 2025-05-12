package com.jbrunoo.digitink.presentation.play.domain.model

data class QnaWithPath(
    val id: Int = -1,
    val qna: Qna,
    val paths: List<DrawPath>,
    val isCorrect: Boolean? = null,
)
