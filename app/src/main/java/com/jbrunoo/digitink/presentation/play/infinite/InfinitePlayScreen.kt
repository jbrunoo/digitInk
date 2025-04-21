package com.jbrunoo.digitink.presentation.play.infinite

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.LifeLayout
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import com.jbrunoo.digitink.presentation.play.model.rememberPlayBoardState

@Composable
fun InfinitePlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    viewModel: InfinitePlayViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is InfinitePlayUIState.LOADING -> CircularProgressIndicator()

        is InfinitePlayUIState.SUCCESS -> {
            val playBoardState = rememberPlayBoardState()

            LaunchedEffect(state.lifeCount) {
                if(state.lifeCount == 0) {
                    playBoardState.changeGameOver() // gameOver 시 자동 스크롤 정지
                    viewModel.saveResultEntry { onTerminate() }
                }
            }

            Column(
                modifier = modifier,
            ) {
                LifeLayout(
                    lifeCount = state.lifeCount,
                )
                PlayBoard(
                    qnaWithPath = state.qnaWithPathList,
                    playBoardState = playBoardState,

                    onUpdateUserPaths = viewModel::onUpdatePaths,
                    onGradeUserDraw = viewModel::onUpdateDrawResult
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