package com.jbrunoo.digitink.presentation.result

import com.jbrunoo.digitink.domain.model.Score

data class ResultUIState(
    val score: Score = Score(),
    val isAuth: Boolean = false,
)
