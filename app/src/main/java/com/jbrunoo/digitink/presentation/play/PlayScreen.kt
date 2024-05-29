package com.jbrunoo.digitink.presentation.play

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jbrunoo.digitink.presentation.play.component.drawCorrectIndicator
import com.jbrunoo.digitink.presentation.play.component.drawIncorrectIndicator
import com.jbrunoo.digitink.presentation.play.component.drawUserPaths
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayScreen(
    navController: NavHostController,
    viewModel: PlayViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val limitTime = viewModel.limitTime.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is PlayUiState.LOADING -> CircularProgressIndicator()
        is PlayUiState.SUCCESS -> {
            var showResultDialog by remember { mutableStateOf(false) }
            Column {
                TimerLayout(
                    limitTime = { limitTime.value },
                    onTerminate = { showResultDialog = true }
                )
                ContentCarousel(
                    qnaList = state.qnaList,
                    pathsList = state.pathsList,
                    onTerminate = { showResultDialog = true },
                    onPathsUpdate = {
                        viewModel.onPathsUpdate(it)
                    },
                    onCheckDrawResult = { bmp, idx ->
                        viewModel.onCheckCorrect(bmp, idx)
                    }
                )
            }
            if (showResultDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showResultDialog = false
                    }, confirmButton = {
                        Button(onClick = {
                            viewModel.resetGame()
                            showResultDialog = false
                        }) {
                            Text(text = "RESTART")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "HOME")
                        }
                    },
                    title = { Text(text = "CONGRATULATIONS") },
                    text = {
                        Text(text = "SCORE: ${viewModel.correctCount}")
                    }
                )
            }
        }
    }
}

@Composable
fun ContentCarousel(
    qnaList: List<QnaState>,
    pathsList: List<List<PathState>>,
    onTerminate: () -> Unit,
    onPathsUpdate: (List<PathState>) -> Unit,
    onCheckDrawResult: (ImageBitmap?, Int) -> Unit
) {
    /* 스크롤 처리 */
    // 0. 다음 문제가 없으면 스크롤 종료
    // 1. 5초 동안 유저 입력 받기
    // 2-1. 유저 입력 없을 시, checkDrawResult == false
    // 2-2. 유저 입력 시, onCheckDrawResult() 후 5초 딜레이 취소
    // 3. 0.5초 후 다음 문제 스크롤
    val listState = rememberLazyListState()
    var autoScroll by remember { mutableStateOf(false) }
    var isDraw by remember { mutableStateOf(false) }
    var delayJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(
        key1 = autoScroll,
        key2 = isDraw
    ) {
        val nextIdx = listState.firstVisibleItemIndex + 1
        if (!isDraw) {
            delayJob = launch {
                try { // cancelException 예외 처리
                    delay(5.seconds)
                    onCheckDrawResult(null, nextIdx - 1) // checkDrawResult == false
                    delay(0.5.seconds)
                    Log.d("autoScroll", "autoScroll")
                    if (nextIdx >= qnaList.size - 1) onTerminate()

                    listState.scrollToItem(nextIdx)
                } finally {
                    autoScroll = !autoScroll
                }
            }
        } else {
            delayJob?.cancelAndJoin()
            delay(0.5.seconds)
            Log.d("ScrollImmediately", "ScrollImmediately")
            if (nextIdx >= qnaList.size - 1) onTerminate()
            listState.scrollToItem(nextIdx)
            isDraw = false
        }
    }

    BoxWithConstraints {
        val maxHeight = this.maxHeight
        val itemDp = (maxHeight - 16.dp) / 5

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            userScrollEnabled = false
        ) {
            items(4) {
                Spacer(modifier = Modifier.height(itemDp))
            }
            itemsIndexed(qnaList) { idx, qnaState ->
                val paths = if (idx < pathsList.size) pathsList[idx] else emptyList()
                val isCurrentQuestion by remember { derivedStateOf { listState.firstVisibleItemIndex == idx } }
                val borderColor = if (isCurrentQuestion) Color.White else Color.Transparent
                Row(
                    modifier = Modifier
                        .height(itemDp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuestionLayout(
                        question = qnaState.question,
                        modifier = Modifier
                            .wrapContentSize()
                            .border(1.dp, borderColor)
                    )
                    DrawDigitLayout(
                        paths = paths,
                        checkDrawResult = qnaState.checkDrawResult,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(itemDp)
                            .clipToBounds() // 외부까지 그려지는 것 방지, ui에서 그려지지 않을 뿐 드래그 로그는 찍힘
                            .border(1.dp, borderColor),
                        onCheckDrawResult = { bmp ->
                            isDraw = true
                            onCheckDrawResult(bmp, idx)
                        },
                        onPathsUpdate = { onPathsUpdate(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimerLayout(limitTime: () -> Long, onTerminate: () -> Unit) {
    val time = limitTime()
    val seconds = time / 1000
    val milliSeconds = (time % 1000) / 10
    if (time == 0L) onTerminate()
    Text(
        text = String.format("Timer: %d.%02d", seconds, milliSeconds),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )
    )
}

@Composable
fun QuestionLayout(question: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Text(
            question, style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Composable
fun DrawDigitLayout(
    paths: List<PathState>,
    checkDrawResult: Boolean?,
    modifier: Modifier = Modifier,
    onPathsUpdate: (List<PathState>) -> Unit,
    onCheckDrawResult: (ImageBitmap) -> Unit,
) {
    val userPaths = remember { mutableStateListOf<PathState>() }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(imageBitmap) {
        imageBitmap?.let {
            onPathsUpdate(userPaths)
            onCheckDrawResult(it)
        }
    }

    val drawModifier = modifier
        .pointerInput(Unit) {
            if (checkDrawResult == null) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            async {
                                imageBitmap = graphicsLayer.toImageBitmap()
                            }.await()
                        }
                    }
                ) { change, dragAmount ->
//                Log.d("change", "$change, $dragAmount")
                    val path =
                        PathState(
                            start = change.position - dragAmount,
                            end = change.position
                        )
                    userPaths.add(path)
                }
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

    Canvas(
        modifier = if (paths.isNotEmpty()) modifier else drawModifier
    ) {
        drawUserPaths(paths.ifEmpty { userPaths })
        checkDrawResult?.let {
            if (it) drawCorrectIndicator()
            else drawIncorrectIndicator()
        }
    }
}