package com.jbrunoo.digitink.presentation.home.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.designsystem.component.DiButton
import com.jbrunoo.digitink.designsystem.theme.DigitInkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentTicketCount: Int = 0,
    isRewardAdLoaded: Boolean = false,
    millisUntilNextTicket: Long = 0L,
    canWatchRewardAd: Boolean = false,
    onPlayNormal: (Int) -> Unit = {},
    onPlayInfinite: () -> Unit = {},
    onClickAd: () -> Unit = {},
    onClickResult: () -> Unit = {},
) {
    var expandedCount by remember { mutableStateOf(false) }
    var expandedInfo by remember { mutableStateOf(false) }
    var expandedTicket by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        HomeTopBar(
            onClickInfo = { expandedInfo = true },
            modifier = Modifier.align(Alignment.TopCenter),
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-24).dp),
        ) {
            DiButton(
                onClick = { expandedTicket = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(25.dp),
            ) {
                TextWithTicket(
                    suffixText = "$currentTicketCount",
                    trailingIcon = Icons.Default.Add,
                )
            }
            if (millisUntilNextTicket > 0L) {
                Text(
                    text = "Next ticket ${millisUntilNextTicket.toTicketTimerText()}",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DiButton(
                onClick = { expandedCount = true },
                enabled = currentTicketCount >= 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            ) {
                TextWithTicket(
                    prefixText = stringResource(R.string.game_mode_nomal),
                    suffixText = "x 1",
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
//            Text()
            DiButton(
                onClick = onPlayInfinite,
                enabled = currentTicketCount >= 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            ) {
                TextWithTicket(
                    prefixText = stringResource(R.string.game_mode_infinite),
                    suffixText = "x 3",
                )
            }
        }

        DiButton(
            onClick = onClickResult,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
        ) {
            Text(
                text = stringResource(R.string.game_result_text),
            )
        }
    }

    if (expandedCount) {
        QuestionCountBtmSht(
            onSelectCount = {
                onPlayNormal(it)
                expandedCount = false
            },
            onDismissRequest = { expandedCount = false },
        )
    }

    if (expandedInfo) {
        PlayInfoBtmSht(
            onDismissRequest = { expandedInfo = false },
        )
    }

    if (expandedTicket) {
        HomeTicketModal(
            currentTicketCount = currentTicketCount,
            isRewardAdLoaded = isRewardAdLoaded,
            canWatchRewardAd = canWatchRewardAd,
            nextTicketText = millisUntilNextTicket.takeIf { it > 0L }?.toTicketTimerText(),
            onClickAd = onClickAd,
            onDismiss = { expandedTicket = false },
        )
    }
}

private fun Long.toTicketTimerText(): String {
    val totalSeconds = (this / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Preview
@Composable
private fun HomeScreenPreview() {
    DigitInkTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            HomeScreen()
        }
    }
}
