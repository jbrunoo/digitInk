package com.jbrunoo.digitink.presentation.play.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbrunoo.digitink.R
import java.util.Locale

@Composable
internal fun TimerLayout(
    modifier: Modifier = Modifier,
    limitTime: () -> Long,
    onTerminate: () -> Unit,
) {
    val time = limitTime()
    val seconds = time / 1000
    val milliSeconds = (time % 1000) / 10

    if (time == 0L) onTerminate()

    Text(
        text = String.format(
            Locale.ROOT,
            stringResource(R.string.game_timer_text),
            seconds,
            milliSeconds,
        ),
        modifier = modifier.padding(top = 16.dp),
        fontSize = 24.sp,
    )
}

@Preview
@Composable
private fun TimerLayoutPreview() {
    TimerLayout(limitTime = { 3 }) { }
}
