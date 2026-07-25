package com.iqbalfauzi.kitchenstock.presentation.shopping

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.presentation.inventory_detail.InventoryDetailContent
import com.iqbalfauzi.kitchenstock.presentation.inventory_detail.model.InventoryDetailUiState
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstock.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingItemContent(
    uiState: AddShoppingItemUiState,
    onIntent: (AddShoppingItemIntent) -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Shopping Item",
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
            // Product Name
            Text(
                text = "Product Name",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            ProductSelection(
                name = uiState.name,
                onNameChange = { onIntent(AddShoppingItemIntent.UpdateName(it)) }
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
            ) { onIntent(AddShoppingItemIntent.SelectCategory(it)) }

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
                        onQuantityChanged = { onIntent(AddShoppingItemIntent.UpdateQuantity(it)) }
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
                        onUnitSelected = { onIntent(AddShoppingItemIntent.UpdateUnit(it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(spacing.xl))

            Button(
                onClick = { if (!uiState.isSaving && !uiState.isSuccess) onIntent(AddShoppingItemIntent.Save) },
                enabled = !uiState.isSaving && uiState.name.isNotBlank(),
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
                        if (uiState.isSuccess) Icons.Default.Check else Icons.Default.ShoppingCart,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = when {
                            uiState.isSuccess -> "Added!"
                            uiState.isSaving -> "Adding..."
                            else -> "Add to Shopping List"
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
    categories: List<Category>,
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

@Preview
@Composable
fun InventoryDetailContentLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        InventoryDetailContent(
            uiState = InventoryDetailUiState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
fun InventoryDetailContentDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        InventoryDetailContent(
            uiState = InventoryDetailUiState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}
