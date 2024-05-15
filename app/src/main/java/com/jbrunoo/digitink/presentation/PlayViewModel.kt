package com.jbrunoo.digitink.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(PlayUiState())
    val uiState: StateFlow<PlayUiState> = _uiState.asStateFlow()

    var userGuess by mutableIntStateOf(0)
        private set

    private var answers: MutableList<String> = mutableListOf()
    private lateinit var currentAnswer: String


    init {
        resetGame()
    }

    private fun generateRandomQnA(): List<String> {
        val num1 = (1..9).random()
        val num2 = (1..9).random()
        val operator = listOf("+", "-").random()

//        answer =
        return emptyList()
    }

    fun updateUserAnswer(answer: Int) {
        userGuess = answer
    }

    fun checkUserQuess() {
//        if(userGuess == )
    }

    fun resetGame() {

        _uiState.value = PlayUiState()

    }


}

data class PlayUiState(
    val currentTime: Int = 0,
    val questionList: List<String> = emptyList(),
    val loading: Boolean = false
)