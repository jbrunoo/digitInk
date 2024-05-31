package com.jbrunoo.digitink.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.domain.ResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(resultRepository: ResultRepository) :
    ViewModel() {
    val state: StateFlow<Result> =
        resultRepository.readResult().stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result()
        )


}