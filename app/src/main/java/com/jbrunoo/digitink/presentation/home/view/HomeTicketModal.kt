package com.jbrunoo.digitink.presentation.home.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbrunoo.digitink.R

@Composable
fun HomeTicketModal(
    currentTicketCount: Int,
    modifier: Modifier = Modifier,
    onClickAd: () -> Unit,
    onDismiss: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (isPressed) 0.9f else 1f,
        label = "scale",
    )

    BackHandler(onBack = onDismiss)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
    ) {
        TextWithTicket(
            suffixText = "$currentTicketCount",
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.TopCenter),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(stringResource(R.string.home_ticket_modal_get_more_ticket_text), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .scale(scale)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                val released =
                                    try {
                                        awaitRelease()
                                        true
                                    } catch (e: Exception) {
                                        false
                                    }
                                isPressed = false
                                if (released) onClickAd()
                            },
                        )
                    },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp)),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ticket_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFF06292),
                                            Color(0xFFF8BBD0),
                                        ),
                                    ),
                                ),
                            tint = Color.Unspecified,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.home_ticket_modal_watch_ads_text),
                                color = Color.Blue,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painterResource(R.drawable.arrowtriangle_right_circle),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Blue,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(40.dp)
                        .offset(y = (-20).dp)
                        .background(
                            color = Color(0xFC9857D7),
                            shape = RoundedCornerShape(12.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "x3",
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = onDismiss,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close ticket modal",
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTicketModalPreview() {
    HomeTicketModal(
        currentTicketCount = 5,
        onClickAd = {},
        onDismiss = {},
    )
}
