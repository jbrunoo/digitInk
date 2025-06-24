package com.jbrunoo.digitink.presentation.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.designsystem.component.LinkifyText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayInfoBtmSht(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
) {
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(scrollState),
        ) {
            Text(stringResource(R.string.game_rules_text), fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.rule_model_text), fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.rule_model_detail_text))
            Text(stringResource(R.string.rule_limit_time_text), fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.rule_limit_time_detail_text))
            Text(stringResource(R.string.rule_score_text), fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.rule_score_detail_text))
            Text(stringResource(R.string.rule_caution_text), fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.rule_caution_detail_text))
            Text(stringResource(R.string.rule_link_text), fontWeight = FontWeight.ExtraBold)
            LinkifyText(stringResource(R.string.rule_link_detail_text))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PlayInfoBtmShtPreview() {
    val sheetState =
        SheetState(
            skipPartiallyExpanded = false,
            initialValue = SheetValue.Expanded,
            density = LocalDensity.current,
            skipHiddenState = false,
        )

    PlayInfoBtmSht(
        sheetState = sheetState,
        onDismissRequest = {},
    )
}
