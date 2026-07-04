package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iqbalfauzi.kitchenstockmobile.domain.model.Category
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstockmobile.ui.theme.LocalSpacing

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
                onNameChange = { onIntent(AddShoppingItemIntent.UpdateName(it)) },
                products = uiState.products,
                onProductSelected = { onIntent(AddShoppingItemIntent.SelectProduct(it.id)) }
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
            CategorySelection(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSelection(
    name: String,
    onNameChange: (String) -> Unit,
    products: List<Product>,
    onProductSelected: (Product) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredProducts = remember(name, products) {
        if (name.isBlank()) {
            products.take(5)
        } else {
            products.filter { it.name.contains(name, ignoreCase = true) }.take(5)
        }
    }

    Box {
        OutlinedTextField(
            value = name,
            onValueChange = {
                onNameChange(it)
                expanded = true
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g., Organic Milk") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        if (expanded && filteredProducts.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                filteredProducts.forEach { product ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(product.name)
                                Text(
                                    text = product.unit,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onProductSelected(product)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategorySelection(
    selectedCategoryId: String,
    categories: List<Category>,
    onCategorySelected: (String) -> Unit
) {
    val spacing = LocalSpacing.current

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            val isSelected = category.id == selectedCategoryId
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category.id) },
                label = { Text(category.name) },
                leadingIcon = {
                    Text(category.icon ?: "🏷️")
                },
                shape = MaterialTheme.shapes.medium,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
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
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White),
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
    val units = listOf("Units", "kg", "g", "L", "ml", "Packs", "butir")

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
fun AddShoppingItemContentPreview() {
    val dummyProducts = listOf(
        Product("1", null, "Bananas", unit = "bunch"),
        Product("2", null, "Spinach", unit = "bag"),
        Product("3", null, "Milk", unit = "gallon")
    )
    KitchenStockTheme {
        AddShoppingItemContent(
            uiState = AddShoppingItemUiState(
                products = dummyProducts
            ),
            onIntent = {},
            onBackClick = {}
        )
    }
}
