package com.jbrunoo.digitink.presentation.play.domain.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberPlayBoardState(
    isAutoScrollState: MutableState<Boolean> = mutableStateOf(false),
    listState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(Unit) {
    PlayBoardState(
        isAutoScrollState = isAutoScrollState,
        listState = listState,
        scope = scope,
    )
}

class PlayBoardState(
    val isAutoScrollState: MutableState<Boolean>,
    val listState: LazyListState,
    private val scope: CoroutineScope,
) {
    var currentIdx = mutableIntStateOf(0)
    private var isGameOver = false
    private var job: Job? = null
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, exception ->
            Timber.e("Exception in coroutine: $exception")
        }

    private fun switchAutoScrollState() {
        isAutoScrollState.value = !isAutoScrollState.value
    }

    fun startAutoScroll(onGradeUserDraw: () -> Unit) {
        job =
            scope.launch(coroutineExceptionHandler) {
                Timber.d("start auto scroll")

                delay(5.seconds)
                onGradeUserDraw()
                delay(0.5.seconds)

                animateScroll()
                switchAutoScrollState()
            }
    }

    fun cancelAutoScroll(onGradeUserDraw: () -> Unit) {
        scope.launch(coroutineExceptionHandler) {
            Timber.d("cancel auto scroll")

            job?.cancelAndJoin()
            onGradeUserDraw()
            delay(0.5.seconds)

            animateScroll()
            switchAutoScrollState()
        }
    }

    private fun animateScroll() {
        if (isGameOver) return

        scope.launch(Dispatchers.Main) {
            Timber.d("animate auto scroll : ${currentIdx.intValue + 1}")

            listState.animateScrollToItem(++currentIdx.intValue)
        }
    }

    fun changeGameOver() {
        isGameOver = true
    }
}
