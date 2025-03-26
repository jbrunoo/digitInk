package com.jbrunoo.digitink.presentation.result

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.presentation.component.BigText

private const val DELIMITER = ": "

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navigateToHome: () -> Unit = {},
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as Activity
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(12.dp),
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
            Column {
                ResultSet(result = uiState.result)
                Button(
                    onClick = { isDialogOpen = true },
                    modifier = Modifier.fillMaxWidth(0.3f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "clear score history"
                    )
                }
            }
            Button(
                onClick = { viewModel.showLeaderBoard(activity) },
                modifier = Modifier.fillMaxWidth(0.3f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Image(
                    painterResource(R.drawable.games_leaderboards_white),
                    null
                )
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
private fun ResultSet(result: Result) {
    val countList = listOf(5, 10, 15, 20)

    Column {
        for ((idx, count) in countList.withIndex()) {
            BigText(
                text = stringResource(
                    R.string.question_count_prefix,
                    count
                ) + DELIMITER + result.find(count),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            if (idx != countList.size - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Preview
@Composable
private fun ResultScreenPreview() {
    ResultScreen()
}