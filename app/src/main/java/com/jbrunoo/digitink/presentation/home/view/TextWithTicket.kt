package com.jbrunoo.digitink.presentation.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.designsystem.component.BigText

@Composable
fun TextWithTicket(
    modifier: Modifier = Modifier,
    prefixText: String = "",
    suffixText: String = "",
    trailingIcon: ImageVector? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (prefixText.isNotBlank()) {
            BigText(text = prefixText, modifier = Modifier.padding(end = 8.dp)) // fontSize = 16.sp
        }
        Image(
            painter = painterResource(R.drawable.ticket_icon),
            contentDescription = "game play ticket",
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = suffixText, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        if (trailingIcon != null) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Gray, shape = RoundedCornerShape(50)),
            ) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "add game tickets",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Preview(apiLevel = 34, showBackground = true)
@Composable
private fun TextWithTicketPreview() {
    TextWithTicket(
        prefixText = "prefix text",
        suffixText = "suffix text",
    )
}
