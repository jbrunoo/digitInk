package com.jbrunoo.digitink.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp)
            .border(width = 1.dp, color = if (enabled) Color.White else Color.Gray, shape = shape),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 4.dp,
            disabledElevation = 4.dp,
        ),
        content = content,
    )
}

@Preview
@Composable
private fun DiButtonPreview() {
    DiButton(onClick = {}) {
        Text("button")
    }
}