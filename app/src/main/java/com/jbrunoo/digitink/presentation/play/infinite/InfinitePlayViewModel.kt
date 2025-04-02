package com.jbrunoo.digitink.presentation.play.infinite

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.Classifier
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.playgames.PlayGamesManager
import com.jbrunoo.digitink.presentation.play.normal.PathState
import com.jbrunoo.digitink.presentation.play.normal.QnaState
import com.jbrunoo.digitink.utils.Constants.DATASTORE_KEY_INFINITE
import com.jbrunoo.digitink.utils.Constants.LEADERBOARD_KEY_INFINITE
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
    private val classifier: Classifier,
    private val resultRepository: ResultRepository,
    private val playGamesManager: PlayGamesManager,
) : ViewModel() {

    private val _correctCount = MutableStateFlow(0)
    private val _lifeCount = MutableStateFlow(5)
    private val _qnaList = MutableStateFlow<List<QnaState>>(emptyList())
    private val _pathsList = MutableStateFlow<List<List<PathState>>>(emptyList())

    val uiState: StateFlow<InfinitePlayUIState> =
        combine(_lifeCount, _qnaList, _pathsList) { lifeCount, qnaList, pathsList ->
            InfinitePlayUIState.SUCCESS(
                lifeCount = lifeCount,
                qnaList = qnaList,
                pathsList = pathsList
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InfinitePlayUIState.LOADING
        )

    init {
        generateQnaPairs(5)
        _pathsList.value = List(5) { emptyList() }

        viewModelScope.launch(Dispatchers.Main) {
            _correctCount.collect {
                Timber.d("infinite mode - 1 qna generating")
                generateQnaPairs(1)
            }
        }
    }

    private fun generateQnaPairs(count: Int) {
        viewModelScope.launch {
            val qnaMutableList = mutableListOf<QnaState>()
            val pathsMutableList = mutableListOf<List<PathState>>()

            repeat(count) {
                // 1~9가 답이 되는 문제만 선택
                while (true) {
                    val num1 = (1..9).random()
                    val num2 = (1..9).random()
                    val operator = listOf("+", "-").random()
                    val result = if (operator == "+") num1 + num2 else num1 - num2
                    if (result in 1..9) { // mnist가 단일 값만 비교되서 단일 결과 값을 가지는 경우만
                        val qnaState = QnaState("$num1 $operator $num2 =", result, null)
                        qnaMutableList.add(qnaState)
                        pathsMutableList.add(emptyList())
                        break
                    }
                }
            }
            _qnaList.update { it + qnaMutableList }
            _pathsList.update { it + pathsMutableList }
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

            if (checkDrawResult) _correctCount.value++ else _lifeCount.value--

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

    fun saveResultEntry(onComplete: () -> Unit) {
        val score = calcScore()

        viewModelScope.launch(Dispatchers.IO) {
            playGamesManager.submitScore(LEADERBOARD_KEY_INFINITE, score)
            resultRepository.saveValue(DATASTORE_KEY_INFINITE, score)

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    private fun calcScore(): Long = _correctCount.value * 5L
}