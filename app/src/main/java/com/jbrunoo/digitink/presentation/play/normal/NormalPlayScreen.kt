package com.jbrunoo.digitink.presentation.play.normal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import com.jbrunoo.digitink.presentation.play.component.TimerLayout
import timber.log.Timber

@Composable
fun NormalPlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    viewModel: NormalPlayViewModel = hiltViewModel(),
) {
    Timber.d("normal node start")

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val limitTime = viewModel.limitTime.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is NormalPlayUIState.LOADING -> CircularProgressIndicator()

        is NormalPlayUIState.SUCCESS -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TimerLayout(
                    limitTime = { limitTime.value },
                    onTerminate = {
                        viewModel.saveResultEntry { onTerminate() }
                    },
                )
                PlayBoard(
                    qnaList = state.qnaList,
                    pathsList = state.pathsList,
                    onPathsUpdate = { paths, idx ->
                        viewModel.onPathsUpdate(paths, idx)
                    },
                    onCheckDrawResult = { bmp, idx ->
                        viewModel.onCheckCorrect(bmp, idx)
                    },
                    onTerminate = {
                        viewModel.saveResultEntry { onTerminate() }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PlayScreenPreview() {
    NormalPlayScreen()
}
