package com.jbrunoo.digitink.presentation

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.digitalink.Ink

@Composable
fun PlayScreen() {
    Column {
        DrawDigit()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawDigit() {

    var changeFlag by remember { mutableIntStateOf(0) }
    val inkBuilder = remember(changeFlag) { Ink.builder() }
    var isDraw by remember { mutableStateOf(false) }
    val currentStroke: Ink.Stroke.Builder? by remember { mutableStateOf(null) }
    val ink = remember(isDraw, changeFlag) { inkBuilder.build() }
    Column {
        Button(
            onClick = {
                changeFlag++
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC91D11),
                contentColor = Color.White
            ),
        ) {
            Text(text = "clear")
        }
        Column(
            modifier = Modifier
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isDraw = true
                            Ink.Stroke.builder().addPoint(
                                Ink.Point.create(
                                    event.x,
                                    event.y,
                                    System.currentTimeMillis()
                                )
                            ).build()
                            Log.d("action-down", "${event.x}, ${event.y}")
                        }

                        MotionEvent.ACTION_MOVE -> {
                            currentStroke?.addPoint(
                                Ink.Point.create(
                                    event.x,
                                    event.y,
                                    System.currentTimeMillis()
                                )
                            )
                            Log.d("action-move", "${event.x}, ${event.y}")
                        }

                        MotionEvent.ACTION_UP -> {
                            currentStroke?.addPoint(
                                Ink.Point.create(
                                    event.x,
                                    event.y,
                                    System.currentTimeMillis()
                                )
                            )
                            Log.d("action-up", "${event.x}, ${event.y}")
                            currentStroke?.let {
                                inkBuilder.addStroke(it.build())
                            }
                            isDraw = false
                        }
                    }
                    true
                }
        ) {

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(bottom = 100.dp)
            ) {
                for (stroke in ink.strokes) {
                    drawPath(
                        path = Path().apply {
                            stroke.points.forEachIndexed { index, point ->
                                if (index == 0) moveTo(point.x, point.y)
                                else lineTo(point.x, point.y)
                            }
                        },
                        color = Color.Black,
                        alpha = 0.8f,
                        style = Stroke(width = 5.dp.toPx())
                    )
                }
            }

        }
    }

}