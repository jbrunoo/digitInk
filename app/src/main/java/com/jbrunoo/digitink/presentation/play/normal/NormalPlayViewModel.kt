package com.jbrunoo.digitink.presentation.play.normal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.repository.ClassifierRepository
import com.jbrunoo.digitink.domain.repository.ScoreRepository
import com.jbrunoo.digitink.presentation.play.domain.model.DrawPath
import com.jbrunoo.digitink.presentation.play.domain.model.Qna
import com.jbrunoo.digitink.presentation.play.domain.model.QnaWithPath
import com.jbrunoo.digitink.presentation.utils.extension.datastoreKey
import com.jbrunoo.digitink.presentation.utils.extension.leaderBoardKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class NormalPlayViewModel @Inject constructor(
    private val classifierRepository: ClassifierRepository,
    private val scoreRepository: ScoreRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val questionCount: Int =
        checkNotNull(savedStateHandle["questionCount"]) // 기본적으로 nullable type

    private var _qnaIdCounter = 1
    private var correctCount by mutableIntStateOf(0)

    private val _limitTime = MutableStateFlow(questionCount * 5000L) // milliseconds
    private val _qnaWithPathList = MutableStateFlow<List<QnaWithPath>>(emptyList())

    val uiState: StateFlow<NormalPlayUIState> =
        combine(_limitTime, _qnaWithPathList) { limitTime, qnaWithPathList ->
            NormalPlayUIState.SUCCESS(
                limitTime = limitTime,
                qnaWithPathList = qnaWithPathList,
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NormalPlayUIState.LOADING,
        )

    init {
        resetGame()
    }

    private fun resetGame() {
        generateQnaPairs(questionCount)
        _limitTime.value = questionCount * 5000L
        correctCount = 0
        countDownTime()
    }

    private fun countDownTime() {
        viewModelScope.launch {
            while (_limitTime.value > 0L) {
                delay(10L)
                _limitTime.update { it - 10L }
            }
        }
    }

    private fun generateQnaPairs(questionCount: Int) {
        viewModelScope.launch {
            val tempQnaWithPathList = mutableListOf<QnaWithPath>()

            repeat(questionCount) {
                while (true) {
                    val num1 = (1..9).random()
                    val num2 = (1..9).random()
                    val operator = listOf("+", "-").random()
                    val result = if (operator == "+") num1 + num2 else num1 - num2

                    if (result in 1..9) { // mnist가 단일 값만 비교되서 단일 결과 값을 가지는 경우만
                        val qna = Qna("$num1 $operator $num2 =", result)
                        tempQnaWithPathList.add(
                            QnaWithPath(
                                id = ++_qnaIdCounter,
                                qna = qna,
                                paths = emptyList(),
                                isCorrect = null,
                            ),
                        )
                        break
                    }
                }
            }

            _qnaWithPathList.update { tempQnaWithPathList }
        }
    }

    private fun classifyBmp(bmp: ImageBitmap?): Int? = bmp?.let {
        classifierRepository.classify(bmp)
    }

    fun onCheckCorrect(
        bmp: ImageBitmap?,
        index: Int,
    ) {
        val userGuess = classifyBmp(bmp)

        _qnaWithPathList.update { old ->
            val new = old.toMutableList()
            val tempQnaWithPath = new[index]
            val isCorrect = userGuess?.let { it == tempQnaWithPath.qna.answer } ?: false

            if (isCorrect) correctCount++

            new[index] = tempQnaWithPath.copy(isCorrect = isCorrect)
            new
        }
    }

    fun onPathsUpdate(
        paths: List<DrawPath>,
        index: Int,
    ) {
        _qnaWithPathList.update { old ->
            val new = old.toMutableList()
            val tempQnaWithPath = new[index]

            new[index] = tempQnaWithPath.copy(paths = paths)
            new
        }
    }

    fun saveResultEntry(onComplete: () -> Unit) {
        val score = calcScore()
        val dataStoreKey = questionCount.datastoreKey() ?: return
        val leaderBoardKey = questionCount.leaderBoardKey() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            TODO("submit score")
//            playGamesManager.submitScore(leaderBoardKey, score)
            scoreRepository.saveLocalScore(dataStoreKey, score)

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    private fun calcScore(): Long {
        val score = (correctCount.toDouble() / questionCount) * 100 + (_limitTime.value / 1000.0)
        return (score * 100).roundToLong()
    }
}
