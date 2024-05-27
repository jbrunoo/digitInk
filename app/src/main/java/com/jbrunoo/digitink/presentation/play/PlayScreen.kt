package com.jbrunoo.digitink.presentation.play

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalConfiguration
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    navController: NavHostController,
    viewModel: PlayViewModel = hiltViewModel()
) {
    val uiState = viewModel.playUiState.collectAsStateWithLifecycle()

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
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(title = {
                            TimerLayout(timer = state.timer,
                                onTerminate = { showDialog = true })
                        })
                    }
                ) { paddingValues ->
                    ContentCarousel(
                        paddingValues = paddingValues,
                        qnaList = state.qnaList,
                        pathsList = state.pathsList,
                        onTerminate = { showDialog = true },
                        onPathsUpdate = {
                            viewModel.onPathsUpdate(it)
                        },
                        onCheckDrawResult = { bmp, idx ->
                            viewModel.onCheckCorrect(bmp, idx)
                        }
                    )
                }
            }
        }

        is PlayUiState.Error -> Text(text = state.message)
    }
}

@Composable
fun ContentCarousel(
    paddingValues: PaddingValues,
    qnaList: List<QnaState>,
    pathsList: List<List<PathState>>,
    onTerminate: () -> Unit,
    onPathsUpdate: (List<PathState>) -> Unit,
    onCheckDrawResult: (ImageBitmap?, Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val topPadding = paddingValues.calculateTopPadding()
    val itemHeight = (configuration.screenHeightDp.dp - topPadding) / 5

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

    LaunchedEffect(key1 = autoScroll, key2 = isDraw) { // Unit -> userDrawKey: animateScroll 전에 업데이트 방지
        val nextIdx = listState.firstVisibleItemIndex + 1
        if (nextIdx >= qnaList.size) {
            onCheckDrawResult(null, nextIdx - 1)
            onTerminate()
        }
        if(!isDraw) {
            delayJob = launch {
                try {
                    delay(5.seconds)
                    onCheckDrawResult(null, nextIdx - 1) // checkDrawResult == false
                    delay(0.5.seconds)
                    Log.d("autoScroll", "autoScroll")
                    listState.scrollToItem(nextIdx)
                } finally {
                    autoScroll = !autoScroll
                }
            }
        } else {
            delayJob?.cancelAndJoin() // cancelException 예외 처리해야 하는지
            delay(0.5.seconds)
            Log.d("ScrollImmediately", "ScrollImmediately")
            listState.scrollToItem(nextIdx)
            isDraw = false
        }
    }


    LazyColumn(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        userScrollEnabled = false
    ) {
        items(4) {
            Spacer(modifier = Modifier.height(itemHeight)) // 화면 높이 이상의 빈 공간
        }
        itemsIndexed(qnaList) { idx, qnaState ->
            val paths = if (idx < pathsList.size) pathsList[idx] else emptyList()
            val isCurrentQuestion by remember { derivedStateOf{ listState.firstVisibleItemIndex == idx } }
            Row(
                modifier = Modifier.height(itemHeight)
            ) {
                QuestionLayout(
                    question = qnaState.question,
                    borderColor = if (isCurrentQuestion) Color.White else Color.Transparent
                )
                DrawDigitLayout(
                    paths = paths,
                    checkDrawResult = qnaState.checkDrawResult,
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
    Text(
        text = "Timer : $ticks",
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )
    )
}

@Composable
fun QuestionLayout(question: String, borderColor: Color) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .border(1.dp, borderColor)
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

    val modifier = Modifier
        .size(200.dp)
        .background(Color.Black)

    val drawModifier = modifier
        .clipToBounds() // 외부까지 그려지는 것 방지, 그려지지만 않을 뿐 드래그 이벤트 중이라 로그는 찍힘
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