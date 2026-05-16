package com.jbrunoo.digitink.presentation.home

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbrunoo.digitink.domain.repository.TicketRepository
import com.jbrunoo.digitink.presentation.utils.RewardAdsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val rewardAdsHelper: RewardAdsHelper,
) : ViewModel() {
    val uiState = combine(
        ticketRepository.readTicket(),
        rewardAdsHelper.isAdLoaded,
    ) { ticket, isRewardAdLoaded ->
        HomeUIState(
            ticketCount = ticket.count,
            isRewardAdLoaded = isRewardAdLoaded,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUIState(),
    )

    fun loadRewardAd(context: Context) {
        rewardAdsHelper.loadRewardAd(context)
    }

    fun showRewardAd(activity: Activity) {
        rewardAdsHelper.showRewardAd(activity) { amount ->
            viewModelScope.launch {
                ticketRepository.plusTickets(amount)
            }
        }
    }

    fun startNormalPlay(onStart: () -> Unit) {
        consumeTicketsAndStart(requiredTicketCount = 1, onStart = onStart)
    }

    fun startInfinitePlay(onStart: () -> Unit) {
        consumeTicketsAndStart(requiredTicketCount = 3, onStart = onStart)
    }

    private fun consumeTicketsAndStart(
        requiredTicketCount: Int,
        onStart: () -> Unit,
    ) {
        viewModelScope.launch {
            ticketRepository.minusTickets(requiredTicketCount)
            onStart()
        }
    }
}
