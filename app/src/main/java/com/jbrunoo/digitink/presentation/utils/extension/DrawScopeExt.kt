package com.jbrunoo.digitink.presentation.utils.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jbrunoo.digitink.presentation.play.domain.model.DrawPath

fun DrawScope.drawUserPaths(pathStates: List<DrawPath>) {
    pathStates.forEach { customPath ->
        val path =
            Path().apply {
                moveTo(customPath.start.x, customPath.start.y)
                lineTo(customPath.end.x, customPath.end.y)
            }
        drawPath(
            path = path,
            color = customPath.color,
            alpha = customPath.alpha,
            style = Stroke(width = customPath.strokeWidth.toPx()),
        )
    }
}

fun DrawScope.drawCorrectIndicator() {
    drawCircle(
        color = Color.Green,
        center = Offset(size.width / 2, size.height / 2),
        radius = size.minDimension / 4,
        style = Stroke(width = 10f),
    )
}

fun DrawScope.drawIncorrectIndicator() {
    drawLine(
        color = Color.Red,
        start = Offset(size.width / 4, size.height / 4),
        end = Offset(size.width * 3 / 4, size.height * 3 / 4),
        strokeWidth = 10f,
    )
    drawLine(
        color = Color.Red,
        start = Offset(size.width * 3 / 4, size.height / 4),
        end = Offset(size.width / 4, size.height * 3 / 4),
        strokeWidth = 10f,
    )
}
