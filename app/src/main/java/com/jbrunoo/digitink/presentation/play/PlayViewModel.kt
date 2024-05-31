package com.jbrunoo.digitink.presentation.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.Classifier
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.utils.GameResultKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val classifier: Classifier,
    private val resultRepository: ResultRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val questionCount: Int =
        checkNotNull(savedStateHandle["questionCount"]) // 기본적으로 nullable type

    private val _limitTime = MutableStateFlow(questionCount * 5000L) // milliseconds
    val limitTime = _limitTime.asStateFlow()

    private var correctCount by mutableIntStateOf(0)

    private val _qnaList = MutableStateFlow<List<QnaState>>(emptyList())
    private val _pathsList = MutableStateFlow<List<List<PathState>>>(List(questionCount) { emptyList()} )

    val uiState: StateFlow<PlayUiState> =
        combine(_qnaList, _pathsList) { qnaList, pathsList ->
            PlayUiState.SUCCESS(
                qnaList = qnaList,
                pathsList = pathsList
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayUiState.LOADING
        )

    init {
        resetGame()
    }

    private fun resetGame() {
        generateQnaPairs(questionCount)
        _pathsList.value = List(questionCount) { emptyList() }
        _limitTime.value = questionCount * 5000L
        correctCount = 0
        countDownTime()
    }

    private fun countDownTime() {
        viewModelScope.launch {
            while(_limitTime.value > 0L) {
                delay(10L)
                _limitTime.update { it - 10L }
            }
        }
    }

    private fun generateQnaPairs(questionCount: Int) {
        viewModelScope.launch {
            val qnaMutableList = mutableListOf<QnaState>()
            repeat(questionCount) {
                while (true) {
                    val num1 = (1..9).random()
                    val num2 = (1..9).random()
                    val operator = listOf("+", "-").random()
                    val result = if (operator == "+") num1 + num2 else num1 - num2
                    if (result in 1..9) { // mnist가 단일 값만 비교되서 단일 결과 값을 가지는 경우만
                        val qnaState = QnaState("$num1 $operator $num2 =", result, null)
                        qnaMutableList.add(qnaState)
                        break // break 빼먹어서 처음으로 OutOfMemoryError 겪음. 모델이랑 그래픽 그리는 부분 문제인 줄..
                    }
                }
            }
            _qnaList.value = qnaMutableList
        }
    }

    private fun classifyBmp(bmp: ImageBitmap?): Int? {
        return bmp?.let {
            classifier.classify(bmp)
        }
    }

    fun onCheckCorrect(bmp: ImageBitmap?, index: Int) {
        _qnaList.update { qnaList ->
            val qnaMuList = qnaList.toMutableList()
            val qnaState = qnaMuList[index]
            val userGuess = classifyBmp(bmp)
            val checkDrawResult = userGuess?.let { it == qnaState.answer } ?: false
            if (checkDrawResult) correctCount++
            qnaMuList[index] = qnaState.copy(checkDrawResult = checkDrawResult)
            qnaMuList
        }
    }

    fun onPathsUpdate(paths: List<PathState>, index: Int) {
        _pathsList.update { currentPathsList ->
            val updatedPathsList = currentPathsList.toMutableList()
            updatedPathsList[index] = paths
            updatedPathsList
        }
    }

    fun saveResultEntry() {
        val score = calcScore().toInt()
        val key = getDataStoreKey(questionCount)
        viewModelScope.launch(Dispatchers.IO) {
            key?.let { resultRepository.saveValue(key, score) }
        }
    }
    private fun getDataStoreKey(questionCount: Int): GameResultKey? {
        return when (questionCount) {
            5 -> GameResultKey.SPEED_GAME_5
            10 -> GameResultKey.SPEED_GAME_10
            15 -> GameResultKey.SPEED_GAME_15
            20 -> GameResultKey.SPEED_GAME_20
            else -> null
        }
    }

    private fun calcScore() = correctCount * (100 / questionCount) + _limitTime.value / 1000
}