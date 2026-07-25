package com.iqbalfauzi.kitchenstock.presentation.inventory_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iqbalfauzi.kitchenstock.presentation.inventory_detail.model.InventoryDetailUiState
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstock.ui.theme.LocalSpacing
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation as StorageLocationDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailContent(
    uiState: InventoryDetailUiState,
    onIntent: (InventoryDetailIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = remember(uiState.expiryDate) {
            try {
                LocalDate.parse(uiState.expiryDate).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
            } catch (_: Exception) {
                null
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date
                        onIntent(InventoryDetailIntent.UpdateExpiryDate(date.toString()))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                text = "Product Name",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            ProductSelection(
                name = uiState.name,
                onNameChange = { onIntent(InventoryDetailIntent.UpdateName(it)) }
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            // Category
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            CategoryDropdown(
                selectedCategoryId = uiState.categoryId,
                categories = uiState.categories
            ) { onIntent(InventoryDetailIntent.SelectCategory(it)) }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Storage Location
            Text(
                text = "Storage Location",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            LocationDropdown(
                selectedLocationId = uiState.storageLocationId,
                locations = uiState.locations
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

            // Min Stock Level
            Text(
                text = "Min Stock Level",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            MinStockStepper(
                minStock = uiState.minStockLevel,
                onMinStockChanged = { onIntent(InventoryDetailIntent.UpdateMinStockLevel(it)) }
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            // Expiry Date
            Text(
                text = "Expiry Date (Optional)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            if (isPressed) {
                showDatePicker = true
            }

            val displayDate = remember(uiState.expiryDate) {
                try {
                    val date = LocalDate.parse(uiState.expiryDate)
                    "${date.day.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
                } catch (_: Exception) {
                    uiState.expiryDate
                }
            }

            OutlinedTextField(
                value = displayDate,
                onValueChange = { },
                readOnly = true,
                singleLine = true,
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("DD/MM/YYYY") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Select Date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(spacing.xl))

            Button(
                onClick = { if (!uiState.isSaving && !uiState.isSuccess) onIntent(InventoryDetailIntent.Save) },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (uiState.isSuccess) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                ),
                contentPadding = PaddingValues(horizontal = spacing.md)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (uiState.isSuccess) Icons.Default.Check else Icons.Default.Inventory,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = when {
                            uiState.isSuccess -> "Added!"
                            uiState.isSaving -> "Saving..."
                            uiState.id == null -> "Add to Inventory"
                            else -> "Update Item"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductSelection(
    name: String,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("e.g., Organic Milk") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun CategoryDropdown(
    selectedCategoryId: String,
    categories: List<com.iqbalfauzi.kitchenstock.domain.model.Category>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    Box {
        OutlinedCard(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = selectedCategory?.icon ?: "🏷️")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedCategory?.name ?: "Select Category",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedCategory == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    leadingIcon = { Text(category.icon ?: "🏷️") },
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LocationDropdown(
    selectedLocationId: String,
    locations: List<StorageLocationDomain>,
    onLocationSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLocation = locations.find { it.id == selectedLocationId }

    fun getIcon(name: String) = when {
        name.contains("Fridge", ignoreCase = true) -> Icons.Default.Kitchen
        name.contains("Pantry", ignoreCase = true) -> Icons.Default.Inventory2
        name.contains("Freezer", ignoreCase = true) -> Icons.Default.AcUnit
        else -> Icons.Default.Coffee
    }

    Box {
        OutlinedCard(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = selectedLocation?.let { getIcon(it.name) } ?: Icons.Default.Kitchen,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedLocation == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedLocation?.name ?: "Select Location",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedLocation == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            locations.forEach { location ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            getIcon(location.name),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = { Text(location.name) },
                    onClick = {
                        onLocationSelected(location.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Double,
    onQuantityChanged: (Double) -> Unit
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
                onClick = { if (quantity > 1.0) onQuantityChanged(quantity - 1.0) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            FilledIconButton(
                onClick = { onQuantityChanged(quantity + 1.0) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
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
    val units = listOf("Units", "kg", "g", "L", "ml", "Packs", "butir")

    Box {
        OutlinedCard(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
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

@Composable
private fun MinStockStepper(
    minStock: Double,
    onMinStockChanged: (Double) -> Unit
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
                onClick = { if (minStock > 0.0) onMinStockChanged(minStock - 1.0) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = if (minStock % 1.0 == 0.0) minStock.toInt().toString() else minStock.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            FilledIconButton(
                onClick = { onMinStockChanged(minStock + 1.0) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Preview
@Composable
fun InventoryDetailContentPreview() {
    KitchenStockTheme {
        InventoryDetailContent(
            uiState = InventoryDetailUiState(
                locations = listOf(
                    StorageLocationDomain("1", "Fridge"),
                    StorageLocationDomain("2", "Pantry")
                )
            ),
            onIntent = {},
            onBackClick = {}
        )
    }
}
