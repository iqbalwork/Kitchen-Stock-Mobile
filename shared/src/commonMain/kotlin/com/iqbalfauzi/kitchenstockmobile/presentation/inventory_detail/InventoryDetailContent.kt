package com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.model.InventoryDetailUiState
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.model.StorageLocation
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstockmobile.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailContent(
    uiState: InventoryDetailUiState,
    onIntent: (InventoryDetailIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (uiState.id == null) "Add Item" else "Update Item",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(spacing.md)
                .verticalScroll(scrollState)
        ) {
            // Item Name
            Text(
                text = "Item Name",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onIntent(InventoryDetailIntent.UpdateName(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Organic Milk") },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            // Storage Location
            Text(
                text = "Storage Location",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            LocationSelection(
                selectedLocation = uiState.location
            ) { onIntent(InventoryDetailIntent.SelectLocation(it)) }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Quantity & Unit
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    QuantityStepper(
                        quantity = uiState.quantity,
                        onQuantityChanged = { onIntent(InventoryDetailIntent.UpdateQuantity(it)) }
                    )
                }
                Spacer(modifier = Modifier.width(spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Unit",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    UnitDropdown(
                        selectedUnit = uiState.unit,
                        onUnitSelected = { onIntent(InventoryDetailIntent.UpdateUnit(it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Expiry Date
            Text(
                text = "Expiry Date (Optional)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            OutlinedTextField(
                value = uiState.expiryDate,
                onValueChange = { onIntent(InventoryDetailIntent.UpdateExpiryDate(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("dd/mm/yyyy") },
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(spacing.xl))

            Button(
                onClick = { onIntent(InventoryDetailIntent.Save) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(horizontal = spacing.md)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = if (uiState.id == null) "Add to Inventory" else "Update Item",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LocationSelection(
    selectedLocation: String,
    onLocationSelected: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    val locations = listOf(
        StorageLocation("Fridge", Icons.Default.Kitchen),
        StorageLocation("Pantry", Icons.Default.Inventory2),
        StorageLocation("Freezer", Icons.Default.AcUnit),
        StorageLocation("Spices", Icons.Default.Coffee)
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        locations.forEach { location ->
            val isSelected = location.name == selectedLocation
            FilterChip(
                selected = isSelected,
                onClick = { onLocationSelected(location.name) },
                label = { Text(location.name) },
                leadingIcon = {
                    Icon(
                        location.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = MaterialTheme.shapes.medium,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    containerColor = Color.White,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = isSelected
                )
            )
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledIconButton(
                onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White),
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            FilledIconButton(
                onClick = { onQuantityChanged(quantity + 1) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White),
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
private fun UnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val units = listOf("Units", "kg", "g", "L", "ml", "Packs")

    Box {
        OutlinedCard(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedUnit, style = MaterialTheme.typography.bodyLarge)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.45f)
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun InventoryDetailContentPreview() {
    KitchenStockTheme {
        InventoryDetailContent(
            uiState = InventoryDetailUiState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}
