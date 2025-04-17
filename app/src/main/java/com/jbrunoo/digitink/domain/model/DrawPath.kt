package com.jbrunoo.digitink.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DrawPath(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.White,
    val alpha: Float = 0.8f,
    val strokeWidth: Dp = 4.dp
)