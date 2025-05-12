package com.jbrunoo.digitink.presentation.play.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbrunoo.digitink.presentation.play.domain.model.DrawPath
import com.jbrunoo.digitink.presentation.play.domain.model.QnaWithPath
import com.jbrunoo.digitink.presentation.play.domain.model.PlayBoardState
import com.jbrunoo.digitink.presentation.play.domain.model.rememberPlayBoardState
import com.jbrunoo.digitink.presentation.utils.extension.drawCorrectIndicator
import com.jbrunoo.digitink.presentation.utils.extension.drawIncorrectIndicator
import com.jbrunoo.digitink.presentation.utils.extension.drawUserPaths
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun PlayBoard(
    qnaWithPath: List<QnaWithPath>,
    playBoardState: PlayBoardState = rememberPlayBoardState(),
    onUpdateUserPaths: (List<DrawPath>, Int) -> Unit,
    onGradeUserDraw: (ImageBitmap?, Int) -> Unit,
) {
    LaunchedEffect(key1 = playBoardState.isAutoScrollState.value) {
        Timber.d("key1 autoscroll : ${playBoardState.isAutoScrollState.value}")

        playBoardState.startAutoScroll {
            onGradeUserDraw(null, playBoardState.currentIdx.intValue)
        }
    }

    BoxWithConstraints {
        val maxHeight = this.maxHeight
        val itemDp = (maxHeight) / 5
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            state = playBoardState.listState,
//            contentPadding = PaddingValues(vertical = 0.5.dp),
            userScrollEnabled = false
        ) {
            items(4) {
                Spacer(modifier = Modifier.height(itemDp))
            }
            itemsIndexed(
                items = qnaWithPath,
                key = { _: Int, value: QnaWithPath -> value.id }
            ) { idx, item ->
                val borderColor =
                    if (idx == playBoardState.currentIdx.intValue) Color.White else Color.Transparent
                Row(
                    modifier = Modifier
                        .height(itemDp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuestionLayout(
                        question = item.qna.question,
                        modifier = Modifier
                            .wrapContentSize()
                            .border(1.dp, borderColor)
                    )
                    DrawDigitLayout(
                        paths = item.paths,
                        isCorrect = item.isCorrect,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(itemDp)
                            .clipToBounds() // 외부까지 그려지는 것 방지, ui에서 그려지지 않을 뿐 드래그 로그는 찍힘
                            .border(1.dp, borderColor),
                        onCheckDrawResult = { bmp ->
                            playBoardState.cancelAutoScroll {
                                onGradeUserDraw(bmp, playBoardState.currentIdx.intValue)
                            }
                        },
                        onPathsUpdate = { bmp ->
                            onUpdateUserPaths(bmp, playBoardState.currentIdx.intValue)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionLayout(question: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(4.dp)
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
private fun DrawDigitLayout(
    paths: List<DrawPath>,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier,
    onPathsUpdate: (List<DrawPath>) -> Unit,
    onCheckDrawResult: (ImageBitmap) -> Unit,
) {
    val userPaths = remember { mutableStateListOf<DrawPath>() }
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
            if (isCorrect == null) {
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
                        DrawPath(
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
        isCorrect?.let {
            if (it) drawCorrectIndicator()
            else drawIncorrectIndicator()
        }
    }
}

@Preview
@Composable
private fun PlayBoardScreenPreview() {
    PlayBoard(
        qnaWithPath = emptyList(),
        onUpdateUserPaths = { _, _ -> },
        onGradeUserDraw = { _, _ -> },
    )
}
