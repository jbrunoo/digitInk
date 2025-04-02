package com.jbrunoo.digitink.presentation.play.infinite

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.presentation.play.component.LifeLayout
import com.jbrunoo.digitink.presentation.play.component.PlayBoard
import timber.log.Timber

@Composable
fun InfinitePlayScreen(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit = {},
    viewModel: InfinitePlayViewModel = hiltViewModel(),
) {
    Timber.d("infinite mode start")

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is InfinitePlayUIState.LOADING -> CircularProgressIndicator()

        is InfinitePlayUIState.SUCCESS -> {
            Column(
                modifier = modifier,
            ) {
                LifeLayout(
                    lifeCount = state.lifeCount,
                )
                PlayBoard(
                    qnaList = state.qnaList,
                    pathsList = state.pathsList,
                    isGameOver = (state.lifeCount == 0),
                    onTerminate = {
                        viewModel.saveResultEntry { onTerminate() }
                    },
                    onPathsUpdate = { paths, idx ->
                        viewModel.onPathsUpdate(paths, idx)
                    },
                    onCheckDrawResult = { bmp, idx ->
                        viewModel.onCheckCorrect(bmp, idx)
                    }
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