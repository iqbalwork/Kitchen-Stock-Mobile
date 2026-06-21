package com.iqbalfauzi.kitchenstockmobile.presentation.pantry

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.ExpiryStatus
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.PantryItem
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.PantryUiState
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstockmobile.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryContent(
    uiState: PantryUiState,
    onIntent: (PantryIntent) -> Unit = {}
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
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Inventory, contentDescription = "Inventory")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CategoryFilterRow(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { onIntent(PantryIntent.SelectCategory(it)) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                uiState.groupedItems.forEach { (location, items) ->
                    item(key = location) {
                        Text(
                            text = location,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = spacing.sm)
                        )
                    }
                    items(items, key = { it.id }) { item ->
                        PantryItemCard(
                            item = item,
                            onUpdateQuantity = { onIntent(PantryIntent.UpdateQuantity(item.id, it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.padding(vertical = spacing.sm)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = null
            )
        }
    }
}

@Composable
private fun PantryItemCard(
    item: PantryItem,
    onUpdateQuantity: (Int) -> Unit
) {
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
                    text = "${item.unit} • ${item.location}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                ExpiryBadge(item.expiryStatus)
                Spacer(modifier = Modifier.height(spacing.xs))
                QuantityToggle(
                    quantity = item.quantity,
                    onUpdateQuantity = onUpdateQuantity
                )
            }
        }
    }
}

@Composable
private fun ExpiryBadge(status: ExpiryStatus) {
    val (backgroundColor, textColor) = when (status) {
        is ExpiryStatus.Warning -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f) to MaterialTheme.colorScheme.onSecondaryContainer
        is ExpiryStatus.Critical -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) to Color.Red
        is ExpiryStatus.Normal -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuantityToggle(
    quantity: Int,
    onUpdateQuantity: (Int) -> Unit
) {
    val spacing = LocalSpacing.current
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = CircleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = { onUpdateQuantity(quantity - 1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = spacing.sm)
            )
            IconButton(
                onClick = { onUpdateQuantity(quantity + 1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview
@Composable
fun PantryContentPreview() {
    KitchenStockTheme {
        PantryContent(
            uiState = PantryUiState(
                groupedItems = mapOf(
                    "FRIDGE" to listOf(
                        PantryItem("1", "Greek Yogurt", 1, "500g", "Fridge", Icons.Default.WaterDrop, ExpiryStatus.Warning("Expires in 3 days")),
                        PantryItem("2", "Chicken Breast", 2, "units", "Fridge", Icons.Default.WaterDrop, ExpiryStatus.Critical("Expires Tomorrow"))
                    ),
                    "PANTRY" to listOf(
                        PantryItem("3", "Avocados", 4, "units", "Pantry", Icons.Default.Eco, ExpiryStatus.Normal()),
                        PantryItem("4", "Jasmine Rice", 1, "1.5kg", "Pantry", Icons.Default.Grain, ExpiryStatus.Normal())
                    )
                )
            )
        )
    }
}
