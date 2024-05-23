package com.jbrunoo.digitink.presentation.play

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.Classifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val classifier: Classifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayUiState>(PlayUiState.Loading)
    private val _pathsList = MutableStateFlow<List<List<PathState>>>(emptyList())

    val uiState = _uiState.combine(_pathsList) { _uiState, _pathsList ->
        when (_uiState) {
            is PlayUiState.Success -> {
                PlayUiState.Success(
                    timer = _uiState.timer,
                    qnaList = _uiState.qnaList,
                    pathsList = _pathsList
                )
            }

            else -> _uiState
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayUiState.Loading
    )

    var correctCount by mutableIntStateOf(0)
        private set

    private val questionCount: Int =
        checkNotNull(savedStateHandle["questionCount"]) // 기본적으로 nullable type

    init {
        resetGame()
    }

    fun resetGame() {
        generateQnaPairs(questionCount)
        _pathsList.value = emptyList()
        correctCount = 0
    }

    private fun generateQnaPairs(questionCount: Int) {
        viewModelScope.launch {
            try {
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
                _uiState.value = PlayUiState.Success(
                    timer = questionCount * 5,
                    qnaList = qnaMutableList
                )
            } catch (e: Exception) {
                Log.d("generate error", e.message.toString())
                _uiState.value = PlayUiState.Error("Generate Error")
            }
        }
    }

    private fun classifyBmp(bmp: ImageBitmap?): Int? {
        return bmp?.let {
            classifier.classify(bmp)
        }
    }

    fun onCheckCorrect(bmp: ImageBitmap?, index: Int) {
        _uiState.update { state ->
            when (state) {
                is PlayUiState.Success -> {
                    val userGuess = classifyBmp(bmp)
                    val qnaList = state.qnaList.toMutableList()
                    val qnaState = state.qnaList[index]
                    val isCorrect = userGuess?.let { it == qnaState.answer } ?: false
                    qnaList[index] = qnaState.copy(isCorrect = isCorrect)
                    state.copy(qnaList = qnaList)
                }

                else -> state
            }
        }
    }

    fun onPathsUpdate(paths: List<PathState>) {
        _pathsList.update { currentPathsList ->
            val updatedPathsList = currentPathsList.toMutableList()
            updatedPathsList.add(paths)
            updatedPathsList
        }
    }


    fun updateCorrectCount() = correctCount++

}


sealed class PlayUiState {
    data class Success(
        val timer: Int,
        val qnaList: List<QnaState>,
        val pathsList: List<List<PathState>> = emptyList()
    ) : PlayUiState()

    data object Loading : PlayUiState()
    data class Error(val message: String) : PlayUiState()
}

data class QnaState(
    val question: String,
    val answer: Int,
    val isCorrect: Boolean? = null
)

data class PathState(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.White,
    val alpha: Float = 0.8f,
    val strokeWidth: Dp = 4.dp
)