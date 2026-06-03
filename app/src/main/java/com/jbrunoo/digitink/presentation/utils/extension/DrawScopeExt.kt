package com.jbrunoo.digitink.presentation.utils.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jbrunoo.digitink.presentation.play.domain.model.DrawPath
import kotlin.math.hypot

fun DrawScope.drawUserPaths(pathStates: List<DrawPath>) {
    pathStates
        .groupContinuousPaths()
        .forEach { strokePaths ->
            val firstPath = strokePaths.first()
            val points = listOf(firstPath.start) + strokePaths.map { it.end }
            val path = points.toSmoothPath()

            drawPath(
                path = path,
                color = firstPath.color,
                alpha = firstPath.alpha,
                style = Stroke(
                    width = firstPath.strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
}

private fun List<DrawPath>.groupContinuousPaths(): List<List<DrawPath>> {
    if (isEmpty()) return emptyList()

    val groupedPaths = mutableListOf<MutableList<DrawPath>>()
    var currentGroup = mutableListOf(first())
    groupedPaths.add(currentGroup)

    drop(1).forEach { path ->
        val previousPath = currentGroup.last()
        if (previousPath.end.distanceTo(path.start) <= CONTINUOUS_PATH_THRESHOLD) {
            currentGroup.add(path)
        } else {
            currentGroup = mutableListOf(path)
            groupedPaths.add(currentGroup)
        }
    }

    return groupedPaths
}

private fun List<Offset>.toSmoothPath(): Path =
    Path().apply {
        if (isEmpty()) return@apply

        moveTo(first().x, first().y)
        if (size == 1) return@apply
        if (size == 2) {
            lineTo(last().x, last().y)
            return@apply
        }

        for (index in 1 until lastIndex) {
            val current = this@toSmoothPath[index]
            val next = this@toSmoothPath[index + 1]
            val midPoint = current.midPointTo(next)

            quadraticTo(
                current.x,
                current.y,
                midPoint.x,
                midPoint.y,
            )
        }

        lineTo(last().x, last().y)
    }

private fun Offset.midPointTo(other: Offset): Offset =
    Offset(
        x = (x + other.x) / 2f,
        y = (y + other.y) / 2f,
    )

private fun Offset.distanceTo(other: Offset): Float =
    hypot(x - other.x, y - other.y)

private const val CONTINUOUS_PATH_THRESHOLD = 1.5f

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
