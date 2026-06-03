package com.jbrunoo.digitink.presentation.home.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jbrunoo.digitink.R
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.designsystem.component.DiButton

@Composable
fun HomeShopModal(
    coinCount: Int,
    infiniteMaxLifeCount: Int,
    nextInfiniteLifeUpgradeCost: Int?,
    canUpgradeInfiniteLife: Boolean,
    modifier: Modifier = Modifier,
    onPurchaseInfiniteLife: () -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(onBack = onDismiss)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
            )
            Text(
                text = "$coinCount",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.home_shop_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF202124))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                        )
                        Text(
                            text = stringResource(R.string.home_shop_infinite_life),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Text(
                        text = "$infiniteMaxLifeCount/${Constants.MAX_INFINITE_LIFE_COUNT}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = nextInfiniteLifeUpgradeCost?.let {
                        stringResource(R.string.home_shop_next_upgrade_text, it)
                    } ?: stringResource(R.string.home_shop_fully_upgraded_text),
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                )
                DiButton(
                    onClick = onPurchaseInfiniteLife,
                    enabled = canUpgradeInfiniteLife,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = nextInfiniteLifeUpgradeCost?.let {
                            stringResource(R.string.home_shop_upgrade_for_text, it)
                        } ?: stringResource(R.string.home_shop_max_level_text),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = onDismiss,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close shop",
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeShopModalPreview() {
    HomeShopModal(
        coinCount = 45,
        infiniteMaxLifeCount = 6,
        nextInfiniteLifeUpgradeCost = 20,
        canUpgradeInfiniteLife = true,
        onPurchaseInfiniteLife = {},
        onDismiss = {},
    )
}
