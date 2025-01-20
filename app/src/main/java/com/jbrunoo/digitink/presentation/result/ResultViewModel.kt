package com.jbrunoo.digitink.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val resultRepository: ResultRepository,
) : ViewModel() {
    private val _result = MutableStateFlow(Result())
    val result = _result.asStateFlow()

    init {
        readResult()
    }

    private fun readResult() {
        viewModelScope.launch {
            resultRepository.readResult().collect {
                _result.value = it
            }
        }
    }

    suspend fun clearResult() = resultRepository.clearResult()
}