package com.iqbalfauzi.kitchenstockmobile.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.AttentionItem
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.AttentionStatus
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.HomeUiState
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstockmobile.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onAddClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Kitchen Stock",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onInventoryClick) {
                        Icon(Icons.Default.Inventory, contentDescription = "Inventory")
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = spacing.md,
                top = spacing.md,
                end = spacing.md,
                bottom = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            item {
                SummarySection(uiState)
            }

            item {
                Text(
                    text = "Needs Attention",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = spacing.sm)
                )
            }

            items(uiState.attentionItems) { item ->
                AttentionListItem(item)
            }
        }
    }
}

@Composable
private fun SummarySection(uiState: HomeUiState) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        // Total Items Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Items",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.totalItems.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Kitchen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            // Expiring Card
            SmallSummaryCard(
                modifier = Modifier.weight(1f),
                label = "Expiring",
                count = uiState.expiringCount,
                countColor = MaterialTheme.colorScheme.secondary,
                icon = Icons.Default.WarningAmber,
                iconTint = MaterialTheme.colorScheme.secondary
            )

            // Out of Stock Card
            SmallSummaryCard(
                modifier = Modifier.weight(1f),
                label = "Out of Stock",
                count = uiState.outOfStockCount,
                countColor = MaterialTheme.colorScheme.tertiary,
                icon = Icons.Default.ErrorOutline,
                iconTint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun SmallSummaryCard(
    label: String,
    count: Int,
    countColor: Color,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(spacing.sm))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = countColor
            )
        }
    }
}

@Composable
private fun AttentionListItem(item: AttentionItem) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(item.status)
        }
    }
}

@Composable
private fun StatusBadge(status: AttentionStatus) {
    val (backgroundColor, textColor) = when (status) {
        is AttentionStatus.Expiring -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f) to MaterialTheme.colorScheme.onSecondaryContainer
        is AttentionStatus.OutOfStock -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f) to MaterialTheme.colorScheme.onTertiaryContainer
        is AttentionStatus.LowStock -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) to MaterialTheme.colorScheme.onErrorContainer
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun HomeContentPreview() {
    KitchenStockTheme {
        HomeContent(
            uiState = HomeUiState(
                totalItems = 42,
                expiringCount = 5,
                outOfStockCount = 3,
                attentionItems = listOf(
                    AttentionItem(
                        "1", "Whole Milk", "200ml remaining",
                        AttentionStatus.Expiring("Exp. in 2 Days"), Icons.Default.WaterDrop
                    ),
                    AttentionItem(
                        "2", "Large Eggs", "0 remaining",
                        AttentionStatus.OutOfStock(), Icons.Default.Egg
                    ),
                    AttentionItem(
                        "3", "Spinach", "1 bag",
                        AttentionStatus.Expiring("Exp. in 1 Day"), Icons.Default.Eco
                    ),
                    AttentionItem(
                        "4", "Bread", "2 slices",
                        AttentionStatus.LowStock(), Icons.Default.BakeryDining
                    )
                )
            )
        )
    }
}
