package com.jbrunoo.digitink.presentation.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jbrunoo.digitink.presentation.Screen
import com.jbrunoo.digitink.presentation.navigateWithPopUp
import com.jbrunoo.digitink.presentation.component.BigText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navHostController: NavHostController,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Box {
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
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
        ) {
            BigText(text = "5q score : ${state.value.speedGame5}")
            BigText(text = "10q score : ${state.value.speedGame10}")
            BigText(text = "15q score : ${state.value.speedGame15}")
            BigText(text = "20q score : ${state.value.speedGame20}")
        }
    }
}