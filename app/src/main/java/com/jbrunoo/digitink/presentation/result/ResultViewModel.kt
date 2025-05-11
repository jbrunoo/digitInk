package com.jbrunoo.digitink.presentation.result

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.repository.ResultRepository
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.playgames.PlayGamesManager
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
    private val resultRepository: ResultRepository,
    private val playGamesManager: PlayGamesManager,
) : ViewModel() {
    private val _result = MutableStateFlow(Result())
    private val _isAuth = MutableStateFlow(false)

    val uiState: StateFlow<ResultUIState> =
        combine(_result, _isAuth) { result, isAuth ->
            ResultUIState(
                result,
                isAuth
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultUIState()
        )

    init {
        readResult()
        isAuthenticated()
    }

    private fun readResult() {
        viewModelScope.launch(Dispatchers.IO) {
            resultRepository.readResult().collect {
                _result.value = it
            }
        }
    }

    private fun isAuthenticated() {
        viewModelScope.launch(Dispatchers.IO) {
            playGamesManager.isAuthenticated.collect {
                _isAuth.value = it
            }
        }
    }

    fun clearResult() {
        viewModelScope.launch(Dispatchers.IO) {
            resultRepository.clearResult()
        }
    }

    fun showLeaderBoard(activity: Activity) {
        playGamesManager.showLeaderBoard(activity)
    }
}
