package com.jbrunoo.digitink.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.presentation.component.BigText
import com.jbrunoo.digitink.presentation.component.DiButton
import com.jbrunoo.digitink.ui.theme.DigitInkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onClickPlay: (Int) -> Unit = {},
    onClickResult: () -> Unit = {},
    onClickMenu: () -> Unit = {},
) {
    var expandedCount by remember { mutableStateOf(false) }
    var expandedInfo by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        HomeTopBar(
            onClickInfo = { expandedInfo = true },
            onClickSetting = onClickMenu,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        DiButton(
            onClick = { expandedCount = true },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(4.dp),
        ) {
            BigText(text = stringResource(R.string.game_mode_nomal))
        }

        DiButton(
            onClick = onClickResult,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(56.dp),
        ) {
            Text(
                text = stringResource(R.string.game_result_text),
            )
        }
    }

    if (expandedCount) {
        QuestionCountBtmSht(
            onSelectCount = {
                onClickPlay(it)
                expandedCount = false
            },
            onDismissRequest = { expandedCount = false }
        )
    }

    if (expandedInfo) {
        PlayInfoBtmSht(
            onDismissRequest = { expandedInfo = false }
        )
    }
}

@Composable
private fun HomeTopBar(
    onClickInfo: () -> Unit,
    onClickSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
        Row {
            IconButton(onClick = onClickInfo) {
                Icon(
                    imageVector = Icons.Default.Info, null,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
//            IconButton(onClick = onClickSetting) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.List, null,
//                    modifier = Modifier.size(40.dp)
//                )
//            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    DigitInkTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen()
        }
    }
}