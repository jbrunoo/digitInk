package com.jbrunoo.digitink.presentation.result

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.model.Score
import com.jbrunoo.digitink.domain.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
) : ViewModel() {
    private val _score = MutableStateFlow(Score())
    private val _isAuth = MutableStateFlow(false)

    val uiState: StateFlow<ResultUIState> =
        combine(_score, _isAuth) { result, isAuth ->
            ResultUIState(
                result,
                isAuth,
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultUIState(),
        )

    init {
        readResult()
        isAuthenticated()
    }

    private fun readResult() {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.readLocalScore().collect {
                _score.value = it
            }
        }
    }

    private fun isAuthenticated() {
        viewModelScope.launch(Dispatchers.IO) {
//            playGamesManager.isAuthenticated.collect {
//                _isAuth.value = it
//            }
        }
    }

    fun clearResult() {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.clearLocalScore()
        }
    }

    fun showLeaderBoard(activity: Activity) {
//        playGamesManager.showLeaderBoard(activity)
    }
}
