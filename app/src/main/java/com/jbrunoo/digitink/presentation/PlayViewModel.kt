package com.jbrunoo.digitink.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.Classifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(private val classifier: Classifier) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayUiState>(PlayUiState.Loading)
    val uiState: StateFlow<PlayUiState> = _uiState.asStateFlow()

    private val _isCorrect = MutableStateFlow<Boolean?>(null)
    val isCorrect: StateFlow<Boolean?> = _isCorrect.asStateFlow()

    var correctCount by mutableIntStateOf(0)
        private set

    init {
        correctCount = 0
        generateQnaPairs(5)
    }

//    private fun resetGame() {
//        correctCount = 0
//    }

    private fun generateQnaPairs(cnt: Int) {
        viewModelScope.launch {
            try {
                val qnaPairMutableList = mutableListOf<Pair<String, Int>>()
                repeat(cnt) {
                    while (true) {
                        val num1 = (1..9).random()
                        val num2 = (1..9).random()
                        val operator = listOf("+", "-").random()
                        val result = if (operator == "+") num1 + num2 else num1 - num2
                        if (result in 1..9) { // mnist가 단일 값만 비교되서 단일 결과 값을 가지는 경우만
                            val pair = Pair("$num1 $operator $num2 =", result)
                            qnaPairMutableList.add(pair)
                            break // break 빼먹어서 처음으로 OutOfMemoryError 겪음. 모델이랑 그래픽 그리는 부분 문제인 줄..
                        }
                    }
                }
                _uiState.value = PlayUiState.Success(
                    timer = cnt * 5,
                    qnaPairList = qnaPairMutableList
                )
            } catch (e: Exception) {
                Log.d("generate error", e.message.toString())
                _uiState.value = PlayUiState.Error("Generate Error")
            }
        }
    }

    fun validateAnswer(bitmap: ImageBitmap?, answer: Int) {
        _isCorrect.value = bitmap?.let {
            val result = classifier.classify(bitmap)
            Log.d("result", "$result")
            result == answer
        } ?: false
    }

    fun updateCorrectCount() = correctCount++

}


sealed class PlayUiState {
    data class Success(
        val timer: Int,
        val qnaPairList: List<Pair<String, Int>>
    ) : PlayUiState()

    data object Loading : PlayUiState()
    data class Error(val message: String) : PlayUiState()
}