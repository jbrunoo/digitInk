package com.jbrunoo.digitink.presentation.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jbrunoo.digitink.presentation.play.component.drawCorrectIndicator
import com.jbrunoo.digitink.presentation.play.component.drawIncorrectIndicator
import com.jbrunoo.digitink.presentation.play.component.drawUserPaths
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayScreen(
    navController: NavHostController,
    viewModel: PlayViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is PlayUiState.Loading -> CircularProgressIndicator()
        is PlayUiState.Success -> {
            var showDialog by remember { mutableStateOf(false) }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    }, confirmButton = {
                        Button(onClick = {
                            viewModel.resetGame()
                            showDialog = false
                        }) {
                            Text(text = "다시 시작")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "홈으로")
                        }
                    },
                    title = { Text(text = "점수") },
                    text = {}
                )
            } else {
                ContentCarousel(
                    timer = state.timer,
                    qnaList = state.qnaList,
                    pathsList = state.pathsList,
                    onTerminate = { showDialog = true },
                    onPathsUpdate = {
                        viewModel.onPathsUpdate(it)
                    },
                    onCheckCorrect = { bmp, idx ->
                        viewModel.onCheckCorrect(bmp, idx)
                    }
                )
            }
        }

        is PlayUiState.Error -> Text(text = state.message)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentCarousel(
    timer: Int,
    qnaList: List<QnaState>,
    pathsList: List<List<PathState>>,
    onTerminate: () -> Unit,
    onPathsUpdate: (List<PathState>) -> Unit,
    onCheckCorrect: (ImageBitmap, Int) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { qnaList.size }
    )
    Column {
        TimerLayout(timer, onTerminate = onTerminate)
        VerticalPager(state = pagerState) { page ->
            val qnaState = qnaList[page]
            val paths = if (page < pathsList.size) pathsList[page] else emptyList()
            Row {
                QuestionLayout(question = qnaState.question)
                DrawDigitLayout(
                    onCheckCorrect = { bmp ->
                        onCheckCorrect(bmp, page)
                    },
                    paths = paths,
                    isCorrect = qnaState.isCorrect,
                    onPathsUpdate = { onPathsUpdate(it) }
                )
            }
        }
    }
}

@Composable
fun TimerLayout(timer: Int, onTerminate: () -> Unit) {
    var ticks by remember { mutableIntStateOf(timer) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            ticks--
            if (ticks == 0) {
                onTerminate()
                break
            }
        }
    }
    Text(text = "$ticks")
}

@Composable
fun QuestionLayout(question: String) {
    Text(question)
}

@Composable
fun DrawDigitLayout(
    onCheckCorrect: (ImageBitmap) -> Unit,
    paths: List<PathState>,
    isCorrect: Boolean?,
    onPathsUpdate: (List<PathState>) -> Unit
) {
    val userPaths = remember { mutableStateListOf<PathState>() }
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var isDrag by remember { mutableStateOf(false) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    if (paths.isNotEmpty()) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Black)
        ) {
            drawUserPaths(paths)
            isCorrect?.let {
                if (it) drawCorrectIndicator()
                else drawIncorrectIndicator()
            }
        }
    } else {
        Canvas(modifier = Modifier
            .size(200.dp)
            .background(Color.Black)
            .clipToBounds() // 외부까지 그려지는 것 방지, 그려지지만 않을 뿐 드래그 이벤트 중이라 로그는 찍힘
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    isDrag = true
//                Log.d("change", "$change, $dragAmount")
                    val path =
                        PathState(
                            start = change.position - dragAmount,
                            end = change.position
                        )
                    userPaths.add(path)
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
    }
    //    LaunchedEffect(isDrag) {
//        coroutineScope.launch {
//            val bmp = graphicsLayer.toImageBitmap()
//            Log.d("bmp", bmp.toString())
//            delay(5000)
//            onValidateCorrect(bmp)
//        }
//    }
    Button(onClick = {
        coroutineScope.launch {
            async {
                imageBitmap = graphicsLayer.toImageBitmap()
            }.await()
            imageBitmap?.let {
                onCheckCorrect(it)
                onPathsUpdate(userPaths)
            }
        }
    }) {
        Text(text = "분류")
    }
}