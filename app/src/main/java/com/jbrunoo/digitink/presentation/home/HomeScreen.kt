package com.jbrunoo.digitink.presentation.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.presentation.component.BigText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickPlay: (Int) -> Unit = {},
    onClickResult: () -> Unit = {},
) {
    val questionCounts = listOf(5, 10, 15, 20)
    var selectedQuestionCount by remember { mutableIntStateOf(questionCounts[0]) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                Box(
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryEditable) // FocusRequester is not initialized 에러
                        .clickable { expanded = true }
                        .size(50.dp)
                        .border(1.dp, Color.White)
                ) {
                    BigText(
                        text = selectedQuestionCount.toString(), modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded },
                    modifier = Modifier.exposedDropdownSize() // same width
                ) {
                    questionCounts.forEach {
                        DropdownMenuItem(text = { Text(it.toString()) },
                            onClick = {
                                selectedQuestionCount = it
                                expanded = !expanded
                            }
                        )
                    }
                }
            }
            Button(
                onClick = { onClickPlay(selectedQuestionCount) }
            ) {
                BigText(text = stringResource(R.string.game_mode_nomal))
            }
        }
        Text(
            text = stringResource(R.string.game_result_text),
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clickable(onClick = onClickResult)
                .align(Alignment.BottomCenter),
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}