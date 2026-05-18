package com.jbrunoo.digitink.presentation.play.normal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import com.jbrunoo.digitink.presentation.play.component.TimerLayout
import com.jbrunoo.digitink.presentation.play.domain.model.rememberPlayBoardState

@Composable
fun NormalPlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    onSubmitScore: (String, Long) -> Unit = { _, _ -> },
    viewModel: NormalPlayViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is NormalPlayUIState.LOADING -> CircularProgressIndicator()

        is NormalPlayUIState.SUCCESS -> {
            val playBoardState = rememberPlayBoardState()

            LaunchedEffect(playBoardState.currentIdx.intValue) {
                if (playBoardState.currentIdx.intValue == state.qnaWithPathList.size) {
                    playBoardState.changeGameOver()
                    viewModel.saveResultEntry(
                        submitRemoteScore = onSubmitScore,
                        onComplete = onTerminate,
                    )
                }
            }

            Column(
                modifier = modifier.padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TimerLayout(
                    limitTime = { state.limitTime },
                    onTerminate = {
                        viewModel.saveResultEntry(
                            submitRemoteScore = onSubmitScore,
                            onComplete = onTerminate,
                        )
                    },
                )
                PlayBoard(
                    qnaWithPath = state.qnaWithPathList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    playBoardState = playBoardState,
                    onUpdateUserPaths = viewModel::onPathsUpdate,
                    onGradeUserDraw = viewModel::onCheckCorrect,
                )
            }
        }
    }
}
