package com.jbrunoo.digitink.presentation.play.infinite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.LifeLayout
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import com.jbrunoo.digitink.presentation.play.domain.model.rememberPlayBoardState

@Composable
fun InfinitePlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    onSubmitScore: (String, Long) -> Unit = { _, _ -> },
    viewModel: InfinitePlayViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is InfinitePlayUIState.LOADING -> CircularProgressIndicator()

        is InfinitePlayUIState.SUCCESS -> {
            val playBoardState = rememberPlayBoardState()

            LaunchedEffect(state.lifeCount) {
                if (state.lifeCount == 0) {
                    playBoardState.changeGameOver() // gameOver 시 자동 스크롤 정지
                    viewModel.saveResultEntry(
                        submitRemoteScore = onSubmitScore,
                        onComplete = onTerminate,
                    )
                }
            }

            Column(
                modifier = modifier.padding(top = 8.dp, bottom = 8.dp),
            ) {
                LifeLayout(
                    lifeCount = state.lifeCount,
                )
                PlayBoard(
                    qnaWithPath = state.qnaWithPathList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    playBoardState = playBoardState,
                    onUpdateUserPaths = viewModel::onUpdatePaths,
                    onGradeUserDraw = viewModel::onUpdateDrawResult,
                )
            }
        }
    }
}

@Preview
@Composable
private fun InfinitePlayScreenPreview() {
    InfinitePlayScreen()
}
