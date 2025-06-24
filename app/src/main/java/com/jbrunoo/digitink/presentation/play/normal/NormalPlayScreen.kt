package com.jbrunoo.digitink.presentation.play.normal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import com.jbrunoo.digitink.presentation.play.component.TimerLayout
import com.jbrunoo.digitink.presentation.play.domain.model.rememberPlayBoardState

@Composable
fun NormalPlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    viewModel: NormalPlayViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is NormalPlayUIState.LOADING -> CircularProgressIndicator()

        is NormalPlayUIState.SUCCESS -> {
            val playBoardState = rememberPlayBoardState()

            LaunchedEffect(playBoardState.currentIdx.intValue) {
                if (playBoardState.currentIdx.intValue == state.qnaWithPathList.size) {
                    playBoardState.changeGameOver()
                    viewModel.saveResultEntry { onTerminate() }
                }
            }

            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                TimerLayout(
                    limitTime = { state.limitTime },
                    onTerminate = {
                        viewModel.saveResultEntry { onTerminate() }
                    },
                )
                PlayBoard(
                    qnaWithPath = state.qnaWithPathList,
                    playBoardState = playBoardState,
                    onUpdateUserPaths = viewModel::onPathsUpdate,
                    onGradeUserDraw = viewModel::onCheckCorrect,
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
