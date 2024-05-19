package com.jbrunoo.digitink.presentation

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.classify
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp

@Composable
fun PlayScreen() {
    var answerTrue by remember { mutableStateOf(false) }
    val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(0.0f, 255.0f))  // 이 줄 추가 안해서 입력값 달랐음
        .add(
            ResizeOp(
                28,
                28,
                ResizeOp.ResizeMethod.BILINEAR
            )
        ) // RGB 이미지만 resizeOp 가능, grayScale 전 실행
//        .add(TransformToGrayscaleOp()) // 회색조 이미지, 라이브러리 tensorflow lite support 0.3.1 필요
        .build()
    val context = LocalContext.current
    val question = generateQnaPair()
    var floatArray by remember { mutableStateOf(floatArrayOf()) }
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }

    Column {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            QuestionView(question = question.first)
//            if (answerTrue) {
//                AnswerView(answer = question.second)
//            } else {
            DrawDigitView {
                bitmap = it
                floatArray = classify(context, it, imageProcessor)
//                    if (question.second == answer) answerTrue = true
            }
//            }
        }
        bitmap?.let { Image(bitmap = it, contentDescription = null) }
        floatArray.forEach {
            Text(text = "$it")
        }
    }
}

@Composable
fun QuestionView(question: String) {
    Text(question)
}

fun generateQnaPair(): Pair<String, Int> {
    while (true) {
        val num1 = (1..9).random()
        val num2 = (1..9).random()
        val operator = if ((0..1).random() == 0) "+" else "-"
        val result = if (operator == "+") num1 + num2 else num1 - num2
        if (result in 1..9) {
            return Pair("$num1 $operator $num2 =", result)
        }
    }
}

@Composable
fun AnswerView(answer: Int) {
    Text(text = "$answer")
}

@Composable
fun DrawDigitView(
    onClick: (ImageBitmap) -> Unit
) {
    val customPaths = remember { mutableStateListOf<CustomPath>() }
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Column(
        modifier = Modifier
            .background(Color.Gray)
    ) {
        Canvas(modifier = Modifier
            .size(200.dp)
            .background(Color.Green)
            .clipToBounds() // 외부까지 그려지는 것 방지, 그려지지만 않을 뿐 드래그 이벤트 중이라 로그는 찍힘
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    Log.d("change", "$change")
                    Log.d("dragAmount", "$dragAmount")
                    val customPath =
                        CustomPath(start = change.position - dragAmount, end = change.position)
                    customPaths.add(customPath)
                }
            }
            .drawWithContent {
                graphicsLayer.record {
                    // draw the contents of the composable into the graphics layer
                    this@drawWithContent.drawContent()
                }
                // draw the graphics layer on the visible canvas
                drawLayer(graphicsLayer)
            }
        ) {
            customPaths.forEach { customPath ->
                val path = Path().apply {
                    moveTo(customPath.start.x, customPath.start.y)
                    lineTo(customPath.end.x, customPath.end.y)
                }
                drawPath(
                    path = path,
                    color = customPath.color,
                    alpha = customPath.alpha,
                    style = Stroke(width = customPath.strokeWidth.toPx())
                )
            }
        }

        Button(onClick = { customPaths.clear() }) {
            Text("클리어")
        }
        Button(onClick = {
            coroutineScope.launch {
                val bitmap = graphicsLayer.toImageBitmap()
                onClick(bitmap)
            }
        }) {
            Text("인식")
        }
    }
}

data class CustomPath(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val alpha: Float = 0.8f,
    val strokeWidth: Dp = 4.dp
)