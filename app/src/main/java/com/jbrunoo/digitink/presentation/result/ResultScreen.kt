package com.jbrunoo.digitink.presentation.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.presentation.Screen
import com.jbrunoo.digitink.presentation.component.BigText
import com.jbrunoo.digitink.presentation.navigateWithPopUp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navHostController: NavHostController,
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.padding(12.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Result", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateWithPopUp(Screen.HOME.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
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
        BigText(text = "5Q score : ${result.speedGame5}")
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        BigText(text = "10Q score : ${result.speedGame10}")
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        BigText(text = "15Q score : ${result.speedGame15}")
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        BigText(text = "20Q score : ${result.speedGame20}")
    }
}