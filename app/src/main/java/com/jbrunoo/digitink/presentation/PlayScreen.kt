package com.jbrunoo.digitink.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlayScreen(
    viewModel: PlayViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isCorrect = viewModel.isCorrect.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is PlayUiState.Loading -> {
            CircularProgressIndicator()
        }

        is PlayUiState.Success -> {
            ContentPager(
                state.timer,
                state.qnaPairList,
                onValidate = { bmp, answer ->
                    viewModel.validateAnswer(bmp, answer)
                },
                isCorrect = isCorrect.value
            )
        }

        is PlayUiState.Error -> {
            Text(text = state.message)
        }
    }
}

@Composable
fun ContentPager(
    timer: Int,
    qnaList: List<Pair<String, Int>>,
    onValidate: (ImageBitmap, Int) -> Unit,
    isCorrect: Boolean?
) {
    Column {
        TimerLayout(timer)
//    VerticalPager(state =) {
//
//    }
        Row {
            QuestionLayout(question = "1+1=")
            DrawDigitLayout(
                onValidateCorrect = { bmp ->
                    onValidate(bmp, qnaList[0].second)
                },
                isCorrect = isCorrect
            )
        }
    }
}

@Composable
fun TimerLayout(timer: Int) {
    Text(text = "$timer")
}

@Composable
fun QuestionLayout(question: String) {
    Text(question)
}

@Composable
fun DrawDigitLayout(
    onValidateCorrect: (ImageBitmap) -> Unit,
    isCorrect: Boolean? = null,
) {
    val userPaths = remember { mutableStateListOf<UserPath>() }
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var isDrag by remember { mutableStateOf(false) }

    Canvas(modifier = Modifier
        .size(200.dp)
        .background(Color.Black)
        .clipToBounds() // 외부까지 그려지는 것 방지, 그려지지만 않을 뿐 드래그 이벤트 중이라 로그는 찍힘
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                isDrag = true
//                Log.d("change", "$change")
//                Log.d("dragAmount", "$dragAmount")
                val userPath =
                    UserPath(start = change.position - dragAmount, end = change.position)
                userPaths.add(userPath)
                isDrag = false
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
        drawUserPaths(userPaths)
        isCorrect?.let {
            if (it) drawCorrectIndicator()
            else drawIncorrectIndicator()
        }

    }
    //    LaunchedEffect(isDrag) {
//        coroutineScope.launch {
//            val bmp = graphicsLayer.toImageBitmap()
//            Log.d("bmp", bmp.toString())
//            delay(5000)
//            onValidateCorrect(bmp)
//        }
//    }
//    var cameraLauncher = rememberLauncherForActivityResult(contract =, onResult =)
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    Button(onClick = {
        coroutineScope.launch {
            async {
                val bmp = graphicsLayer.toImageBitmap()
                imageBitmap = bmp
                delay(5000)
            }.await()
            imageBitmap?.let {
                onValidateCorrect(it)
            }
        }
    }) {
        Text(text = "인식")   
    }
    imageBitmap?.let {
        Image(bitmap = it, contentDescription = null)
    }
}

private fun DrawScope.drawUserPaths(userPaths: SnapshotStateList<UserPath>) {
    userPaths.forEach { customPath ->
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

private fun DrawScope.drawCorrectIndicator() {
    drawCircle(
        color = Color.Green,
        center = Offset(size.width / 2, size.height / 2),
        radius = size.minDimension / 4,
        style = Stroke(width = 10f)
    )
}

private fun DrawScope.drawIncorrectIndicator() {
    drawLine(
        color = Color.Red,
        start = Offset(size.width / 4, size.height / 4),
        end = Offset(size.width * 3 / 4, size.height * 3 / 4),
        strokeWidth = 10f
    )
    drawLine(
        color = Color.Red,
        start = Offset(size.width * 3 / 4, size.height / 4),
        end = Offset(size.width / 4, size.height * 3 / 4),
        strokeWidth = 10f
    )
}

data class UserPath(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.White,
    val alpha: Float = 0.8f,
    val strokeWidth: Dp = 4.dp
)