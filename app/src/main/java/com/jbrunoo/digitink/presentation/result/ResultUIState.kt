package com.jbrunoo.digitink.presentation.result

import com.jbrunoo.digitink.domain.model.Result

data class ResultUIState(
    val result: Result = Result(),
    val isAuth: Boolean = false,
)