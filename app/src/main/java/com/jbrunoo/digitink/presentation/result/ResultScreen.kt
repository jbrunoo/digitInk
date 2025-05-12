package com.jbrunoo.digitink.presentation.result

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.designsystem.component.BigText
import com.jbrunoo.digitink.designsystem.component.DiButton

private const val INFINITE_COUNT_KET = -1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navigateToHome: () -> Unit = {},
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as Activity
    var isDialogOpen by remember { mutableStateOf(false) }
    val normalModes = listOf(5, 10, 15, 20)

    Scaffold(
        modifier = Modifier.padding(horizontal = 12.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.game_result_text),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "arrow back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(normalModes) { count ->
                    ResultCard(
                        count = count,
                        score = uiState.score.find(count),
                        modifier = Modifier.aspectRatio(1f),
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ResultCard(
                        count = "∞",
                        score = uiState.score.find(INFINITE_COUNT_KET),
                        modifier = Modifier.aspectRatio(2f),
                    )
                }
            }

            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                DiButton(
                    onClick = { isDialogOpen = true },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "clear score history",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                DiButton(
                    onClick = { viewModel.showLeaderBoard(activity) },
                    modifier = Modifier.weight(1f),
                ) {
                    Image(
                        painterResource(R.drawable.games_leaderboards_white),
                        null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (isDialogOpen) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.result_screen_delete_button_txt))
            },
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearResult()
                        isDialogOpen = false
                    }
                ) {
                    Text(stringResource(R.string.delete_button_cofirm_txt))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isDialogOpen = false
                    }
                ) {
                    Text(stringResource(R.string.delete_button_dismiss_txt))
                }
            }
        )
    }
}

@Composable
private fun ResultCard(
    count: Any,
    score: Double,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(text = "$count")
        }

        BigText(text = "$score")
    }
}

@Preview
@Composable
private fun ResultScreenPreview() {
    ResultScreen()
}
