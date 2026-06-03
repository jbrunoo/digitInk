package com.jbrunoo.digitink.presentation.play.normal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import com.jbrunoo.digitink.presentation.play.component.TimerLayout
import com.jbrunoo.digitink.presentation.play.domain.model.rememberPlayBoardState

@Composable
fun NormalPlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    onSubmitScore: (String, Long) -> Unit = { _, _ -> },
    onUnlockAchievement: (Int, Int) -> Unit = { _, _ -> },
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
                        unlockAchievement = onUnlockAchievement,
                        onComplete = onTerminate,
                    )
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
                        viewModel.saveResultEntry(
                            submitRemoteScore = onSubmitScore,
                            unlockAchievement = onUnlockAchievement,
                            onComplete = onTerminate,
                        )
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
