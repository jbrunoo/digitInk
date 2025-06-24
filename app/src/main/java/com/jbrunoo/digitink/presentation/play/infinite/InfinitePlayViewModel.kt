package com.jbrunoo.digitink.presentation.play.infinite

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.domain.repository.ClassifierRepository
import com.jbrunoo.digitink.domain.repository.ScoreRepository
import com.jbrunoo.digitink.presentation.play.domain.model.DrawPath
import com.jbrunoo.digitink.presentation.play.domain.model.Qna
import com.jbrunoo.digitink.presentation.play.domain.model.QnaWithPath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfinitePlayViewModel @Inject constructor(
    private val classifierRepository: ClassifierRepository,
    private val scoreRepository: ScoreRepository,
) : ViewModel() {
    private var _qnaIdCounter = 1
    private val _correctCount = MutableStateFlow(0)

    private val _lifeCount = MutableStateFlow(5)
    private val _qnaWithPathList = MutableStateFlow(emptyList<QnaWithPath>())

    val uiState: StateFlow<InfinitePlayUIState> =
        combine(_lifeCount, _qnaWithPathList) { lifeCount, qnaWithPathList ->
            InfinitePlayUIState.SUCCESS(
                lifeCount = lifeCount,
                qnaWithPathList = qnaWithPathList,
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InfinitePlayUIState.LOADING,
        )

    init {
        generateQna(repeatCount = 5)

        viewModelScope.launch(Dispatchers.Main) {
            _correctCount.collect {
                Timber.d("infinite mode - 1 qna generating")
                generateQna()
            }
        }
    }

    private fun generateQna(repeatCount: Int = 1) {
        viewModelScope.launch {
            // 1~9가 답이 되는 문제만 선택
            repeat(repeatCount) {
                while (true) {
                    val num1 = (1..9).random()
                    val num2 = (1..9).random()
                    val operator = listOf("+", "-").random()
                    val result = if (operator == "+") num1 + num2 else num1 - num2
                    if (result in 1..9) { // mnist가 단일 값만 비교되서 단일 결과 값을 가지는 경우만
                        val qna = Qna("$num1 $operator $num2 =", result)

                        addQnaWithPath(qna)
                        break
                    }
                }
            }
        }
    }

    private fun addQnaWithPath(qna: Qna) {
        Timber.d("addQnaWithPath - qna: $qna")

        _qnaWithPathList.update { old ->
            val new =
                QnaWithPath(
                    id = ++_qnaIdCounter,
                    qna = qna,
                    paths = emptyList(),
                    isCorrect = null,
                )

            old + new
        }
    }

    fun onUpdateDrawResult(
        bmp: ImageBitmap?,
        index: Int,
    ) {
        val userGuess = classifyBmp(bmp)

        _qnaWithPathList.update { old ->
            val new = old.toMutableList()
            val oldQnaWithPath = new[index]

            val isCorrect = userGuess?.let { it == oldQnaWithPath.qna.answer } ?: false
            if (isCorrect) _correctCount.value++ else _lifeCount.value--

            new[index] = oldQnaWithPath.copy(isCorrect = isCorrect)
            new
        }
    }

    fun onUpdatePaths(
        paths: List<DrawPath>,
        index: Int,
    ) {
        Timber.d("onPathsUpdate - paths: $paths")

        _qnaWithPathList.update { old ->
            val new = old.toMutableList()
            val oldQnaWithPath = new[index]

            val updatedQnaWithPath = oldQnaWithPath.copy(paths = paths)
            new[index] = updatedQnaWithPath

            new
        }
    }

    fun saveResultEntry(onComplete: () -> Unit) {
        val score = calcScore()
        val dataStoreKey = Constants.DATASTORE_KEY_INFINITE
        val leaderBoardKey = Constants.LEADERBOARD_KEY_INFINITE

        viewModelScope.launch(Dispatchers.IO) {
//            playGamesManager.submitScore(leaderBoardKey, score)
            scoreRepository.saveLocalScore(dataStoreKey, score)

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    private fun classifyBmp(bmp: ImageBitmap?): Int? =
        bmp?.let { classifierRepository.classify(it) }

    private fun calcScore(): Long = _correctCount.value * 5L
}
