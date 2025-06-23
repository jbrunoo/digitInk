package com.jbrunoo.digitink.presentation.home.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.R

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onClickInfo: () -> Unit,
//    onClickSetting: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
        )
        Row {
            IconButton(onClick = onClickInfo) {
                Icon(
                    imageVector = Icons.Default.Info,
                    null,
                    modifier = Modifier.size(28.dp),
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

@Preview(apiLevel = 34, showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    HomeTopBar(
        onClickInfo = {},
    )
}
