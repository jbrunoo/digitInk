package com.jbrunoo.digitink.presentation.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navigateToHome: () -> Unit = {},
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.padding(12.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.game_result_text), style = MaterialTheme.typography.titleLarge)
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
            ResultSet(result = result)
            IconButton(onClick = {
                scope.launch { viewModel.clearResult() }
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "clear score history")
            }
        }
    }
}

@Composable
private fun ResultSet(result: Result) {
    Column {
        BigText(text = stringResource(R.string.game_result_prefix, 5) + result.speedGame5)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        BigText(text = stringResource(R.string.game_result_prefix, 10) + result.speedGame10)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        BigText(text = stringResource(R.string.game_result_prefix, 15) + result.speedGame15)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        BigText(text = stringResource(R.string.game_result_prefix, 20) + result.speedGame20)
    }
}

@Preview
@Composable
private fun ResultScreenPreview() {
    ResultScreen()
}