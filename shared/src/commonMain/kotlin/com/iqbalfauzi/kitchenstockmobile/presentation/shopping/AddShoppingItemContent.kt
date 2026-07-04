package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstockmobile.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingItemContent(
    uiState: AddShoppingItemUiState,
    onProductSelected: (Product?) -> Unit,
    onQuantityChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current

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
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            // Product Selection Section
            Column {
                Text(
                    text = "Select Product",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                ProductDropdownSelector(
                    selectedProduct = uiState.selectedProduct,
                    products = uiState.products,
                    onProductSelected = onProductSelected
                )
            }

            // Quantity Selection Section
            Column {
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentQty = uiState.quantity.toDoubleOrNull() ?: 1.0
                    QuantityStepper(
                        quantity = currentQty,
                        onQuantityChanged = { onQuantityChanged(it.toString()) }
                    )
                    Spacer(modifier = Modifier.width(spacing.md))
                    uiState.selectedProduct?.let { product ->
                        Text(
                            text = product.unit,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Add Button
            Button(
                onClick = { if (!uiState.isLoading && !uiState.isSuccess) onAddClick() },
                enabled = !uiState.isLoading && uiState.selectedProduct != null,
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
                            uiState.isLoading -> "Adding..."
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
private fun ProductDropdownSelector(
    selectedProduct: Product?,
    products: List<Product>,
    onProductSelected: (Product?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val filteredProducts = remember(searchText, products) {
        if (searchText.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedProduct?.name ?: searchText,
            onValueChange = {
                if (selectedProduct == null) {
                    searchText = it
                    expanded = true
                }
            },
            readOnly = selectedProduct != null,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search product...") },
            trailingIcon = {
                if (selectedProduct != null) {
                    IconButton(onClick = {
                        searchText = ""
                        onProductSelected(null)
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Clear Selection")
                    }
                } else {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
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
                                Text(product.name, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Unit: ${product.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onProductSelected(product)
                            expanded = false
                            searchText = ""
                        }
                    )
                }
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
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                products = dummyProducts,
                selectedProduct = dummyProducts.first()
            ),
            onProductSelected = {},
            onQuantityChanged = {},
            onAddClick = {},
            onBackClick = {}
        )
    }
}
