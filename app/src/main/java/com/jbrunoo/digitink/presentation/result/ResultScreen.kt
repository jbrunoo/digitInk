package com.jbrunoo.digitink.presentation.result

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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
            ResultSet(result = uiState.result)
            IconButton(
                onClick = { viewModel.clearResult() }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "clear score history"
                )
            }
            IconButton(
                onClick = { viewModel.showLeaderBoard(activity) }
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "show leaderBoard"
                    )
                    if (!uiState.isAuth) Text("연동하기")
                }
            }
        }
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