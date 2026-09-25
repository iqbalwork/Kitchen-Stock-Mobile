package com.iqbalfauzi.kitchenstock.presentation.pantry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.presentation.pantry.components.QuickAddSheet
import com.iqbalfauzi.kitchenstock.presentation.pantry.model.ExpiryStatus
import com.iqbalfauzi.kitchenstock.presentation.pantry.model.PantryItem
import com.iqbalfauzi.kitchenstock.presentation.pantry.model.PantryUiState
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockShapes
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstock.ui.theme.LocalSpacing
import com.iqbalfauzi.kitchenstock.ui.theme.PrimaryLight
import com.iqbalfauzi.kitchenstock.ui.theme.SecondaryLight
import com.iqbalfauzi.kitchenstock.ui.theme.StatusDanger
import com.iqbalfauzi.kitchenstock.ui.theme.StatusDangerBg
import com.iqbalfauzi.kitchenstock.ui.theme.StatusSafe
import com.iqbalfauzi.kitchenstock.ui.theme.StatusSafeBg
import com.iqbalfauzi.kitchenstock.ui.theme.StatusWarning
import com.iqbalfauzi.kitchenstock.ui.theme.StatusWarningBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryContent(
    uiState: PantryUiState,
    onIntent: (PantryIntent) -> Unit = {},
    onAddClick: () -> Unit = { onIntent(PantryIntent.ShowAddSheet(true)) },
    onItemClick: (String) -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PantryHeaderBar(
                searchQuery = uiState.searchQuery,
                onSearchChange = { onIntent(PantryIntent.Search(it)) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = SecondaryLight,
                contentColor = Color.White,
                shape = KitchenStockShapes.full,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = "Tambah Stok",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Summary Cards Row (Glanceable 3 Kartu)
            SummaryCardsRow(
                expiredCount = uiState.expiredCount,
                nearExpiryCount = uiState.nearExpiryCount,
                totalStockCount = uiState.totalStockCount
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // Filter Lokasi Horizontal
            LocationFilterRow(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onLocationSelected = { onIntent(PantryIntent.SelectCategory(it)) }
            )

            // Reactive Inventory Item List with Pull to Refresh
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { onIntent(PantryIntent.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.items.isEmpty()) {
                    PantryEmptyState(
                        searchQuery = uiState.searchQuery,
                        onAddClick = onAddClick
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = spacing.md,
                            end = spacing.md,
                            top = spacing.xs,
                            bottom = 88.dp // Space for Extended FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        items(uiState.items, key = { it.id }) { item ->
                            PantryItemCard(
                                item = item,
                                onChangeQuantity = { delta ->
                                    onIntent(PantryIntent.ChangeQuantity(item.id, delta))
                                },
                                onClick = { onItemClick(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Quick Add Sheet
    QuickAddSheet(
        isVisible = uiState.isAddSheetVisible,
        storageLocations = uiState.storageLocations,
        categories = uiState.availableCategories,
        onDismiss = { onIntent(PantryIntent.ShowAddSheet(false)) },
        onQuickAdd = { name, quantity, unit, locationId, expiryDate, categoryId, keepOpen ->
            onIntent(
                PantryIntent.QuickAddIngredient(
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    storageLocationId = locationId,
                    expiryDate = expiryDate,
                    categoryId = categoryId,
                    keepOpen = keepOpen
                )
            )
        }
    )
}

@Composable
private fun PantryHeaderBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md)
            .padding(top = spacing.sm, bottom = spacing.xs)
    ) {
        // Baris Judul & Status Chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Chip Offline
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Offline",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Judul Aplikasi
            Text(
                text = "Kitchen Stock",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tombol Filter / Menu
            IconButton(
                onClick = { },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Urutkan & Filter",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.xs))

        // Search Bar Instan
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text(
                    text = "Cari bahan makanan atau bumbu...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchChange("") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Bersihkan pencarian"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.CropFree,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            singleLine = true,
            shape = KitchenStockShapes.full,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Composable
private fun SummaryCardsRow(
    expiredCount: Int,
    nearExpiryCount: Int,
    totalStockCount: Int
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Card 1: Kadaluwarsa (Merah)
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = StatusDangerBg),
            shape = KitchenStockShapes.md,
            border = BorderStroke(1.dp, StatusDanger.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = StatusDanger
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(StatusDanger)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expiredCount.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = StatusDanger
                )
                Text(
                    text = "Kadaluwarsa",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusDanger,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Card 2: Segera Habis (Amber)
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = StatusWarningBg),
            shape = KitchenStockShapes.md,
            border = BorderStroke(1.dp, StatusWarning.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = StatusWarning
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nearExpiryCount.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = StatusWarning
                )
                Text(
                    text = "Segera Habis",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusWarning,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Card 3: Total Stok (Netral)
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = KitchenStockShapes.md,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = PrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = totalStockCount.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total Stok",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LocationFilterRow(
    categories: List<StorageLocation>,
    selectedCategoryId: String?,
    onLocationSelected: (String?) -> Unit
) {
    val spacing = LocalSpacing.current

    LazyRow(
        contentPadding = PaddingValues(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = spacing.xs)
    ) {
        // Chip "Semua"
        item {
            val isSelected = selectedCategoryId == null
            Surface(
                shape = CircleShape,
                color = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.surfaceContainerLow,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .height(48.dp)
                    .clickable { onLocationSelected(null) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "Semua",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Chip Lokasi
        items(categories, key = { it.id }) { category ->
            val isSelected = category.id == selectedCategoryId
            val icon: ImageVector = when {
                category.name.contains("Kulkas", ignoreCase = true) ||
                    category.name.contains("Fridge", ignoreCase = true) -> Icons.Default.Kitchen
                category.name.contains("Freezer", ignoreCase = true) -> Icons.Default.AcUnit
                category.name.contains("Bumbu", ignoreCase = true) -> Icons.Default.Coffee
                else -> Icons.Default.Inventory2
            }

            Surface(
                shape = CircleShape,
                color = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.surfaceContainerLow,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .height(48.dp)
                    .clickable { onLocationSelected(category.id) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun PantryItemCard(
    item: PantryItem,
    onChangeQuantity: (Int) -> Unit,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = KitchenStockShapes.lg,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(spacing.md)
        ) {
            // Baris Atas: Avatar + Judul & Subtitle + Multi-cue Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Box (Tinted)
                val avatarBgColor = when (item.expiryStatus) {
                    is ExpiryStatus.Expired -> StatusDangerBg
                    is ExpiryStatus.NearExpiry -> StatusWarningBg
                    is ExpiryStatus.Safe -> StatusSafeBg
                }
                val avatarTint = when (item.expiryStatus) {
                    is ExpiryStatus.Expired -> StatusDanger
                    is ExpiryStatus.NearExpiry -> StatusWarning
                    is ExpiryStatus.Safe -> StatusSafe
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(KitchenStockShapes.md)
                        .background(avatarBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = avatarTint
                    )
                }

                Spacer(modifier = Modifier.width(spacing.md))

                // Judul & Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (item.category.isNotBlank()) "${item.location} • ${item.category}" else item.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(spacing.sm))

                // Multi-cue Expiry Badge
                MultiCueExpiryBadge(status = item.expiryStatus)
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // Baris Bawah: Sisa Stok Label + Stepper Kuantitas 48dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sisa Stok",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Capsule Stepper
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        // Tombol minus / hapus (48×48 dp)
                        IconButton(
                            onClick = { onChangeQuantity(-1) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            if (item.quantity <= 1) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Kurang ke 0",
                                    modifier = Modifier.size(20.dp),
                                    tint = StatusDanger
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Kurang 1",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Label Jumlah Tabular + Satuan
                        Text(
                            text = "${item.quantity} ${item.unit}",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        // Tombol plus (48×48 dp)
                        IconButton(
                            onClick = { onChangeQuantity(1) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah 1",
                                modifier = Modifier.size(18.dp),
                                tint = PrimaryLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiCueExpiryBadge(status: ExpiryStatus) {
    val (bgColor, textColor, icon) = when (status) {
        is ExpiryStatus.Safe -> Triple(StatusSafeBg, StatusSafe, Icons.Default.Check)
        is ExpiryStatus.NearExpiry -> Triple(StatusWarningBg, StatusWarning, Icons.Default.HourglassEmpty)
        is ExpiryStatus.Expired -> Triple(StatusDangerBg, StatusDanger, Icons.Default.Warning)
    }

    Box(
        modifier = Modifier
            .clip(KitchenStockShapes.sm)
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = textColor
            )
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PantryEmptyState(
    searchQuery: String,
    onAddClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (searchQuery.isNotBlank()) Icons.Default.Search else Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(spacing.md))

            Text(
                text = if (searchQuery.isNotBlank()) "Bahan tidak ditemukan" else "Dapur masih kosong",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = if (searchQuery.isNotBlank()) {
                    "Tidak ada bahan yang cocok dengan \"$searchQuery\""
                } else {
                    "Mulai catat persediaan bahan makanan di dapur Anda"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            Surface(
                shape = KitchenStockShapes.xl,
                color = PrimaryLight,
                modifier = Modifier
                    .height(48.dp)
                    .clickable(onClick = onAddClick)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tambah Bahan Pertama",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PantryContentLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        PantryContent(
            uiState = PantryUiState(
                expiredCount = 1,
                nearExpiryCount = 3,
                totalStockCount = 24,
                categories = listOf(
                    StorageLocation("1", "Kulkas"),
                    StorageLocation("2", "Freezer"),
                    StorageLocation("3", "Rak Bumbu"),
                    StorageLocation("4", "Pantry")
                ),
                items = listOf(
                    PantryItem(
                        id = "1",
                        name = "Susu UHT Cokelat 1L",
                        quantity = 1,
                        unit = "pcs",
                        location = "Kulkas",
                        category = "Dairy",
                        icon = Icons.Default.WaterDrop,
                        expiryStatus = ExpiryStatus.Expired("Expired Kemarin")
                    ),
                    PantryItem(
                        id = "2",
                        name = "Daging Sapi Slice",
                        quantity = 2,
                        unit = "bungkus",
                        location = "Freezer",
                        category = "Daging",
                        icon = Icons.Default.Restaurant,
                        expiryStatus = ExpiryStatus.NearExpiry("2 hari lagi")
                    ),
                    PantryItem(
                        id = "3",
                        name = "Telur Ayam Omega",
                        quantity = 8,
                        unit = "butir",
                        location = "Kulkas",
                        category = "Dairy & Telur",
                        icon = Icons.Default.Eco,
                        expiryStatus = ExpiryStatus.NearExpiry("3 hari lagi")
                    ),
                    PantryItem(
                        id = "4",
                        name = "Bawang Merah",
                        quantity = 500,
                        unit = "gram",
                        location = "Rak Bumbu",
                        category = "Bumbu",
                        icon = Icons.Default.Grain,
                        expiryStatus = ExpiryStatus.Safe("Aman (15 hari)")
                    )
                )
            )
        )
    }
}

@Preview
@Composable
fun PantryContentDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        PantryContent(
            uiState = PantryUiState(
                expiredCount = 1,
                nearExpiryCount = 3,
                totalStockCount = 24,
                categories = listOf(
                    StorageLocation("1", "Kulkas"),
                    StorageLocation("2", "Freezer"),
                    StorageLocation("3", "Rak Bumbu"),
                    StorageLocation("4", "Pantry")
                ),
                items = listOf(
                    PantryItem(
                        id = "1",
                        name = "Susu UHT Cokelat 1L",
                        quantity = 1,
                        unit = "pcs",
                        location = "Kulkas",
                        category = "Dairy",
                        icon = Icons.Default.WaterDrop,
                        expiryStatus = ExpiryStatus.Expired("Expired Kemarin")
                    ),
                    PantryItem(
                        id = "2",
                        name = "Daging Sapi Slice",
                        quantity = 2,
                        unit = "bungkus",
                        location = "Freezer",
                        category = "Daging",
                        icon = Icons.Default.Restaurant,
                        expiryStatus = ExpiryStatus.NearExpiry("2 hari lagi")
                    ),
                    PantryItem(
                        id = "3",
                        name = "Telur Ayam Omega",
                        quantity = 8,
                        unit = "butir",
                        location = "Kulkas",
                        category = "Dairy & Telur",
                        icon = Icons.Default.Eco,
                        expiryStatus = ExpiryStatus.NearExpiry("3 hari lagi")
                    ),
                    PantryItem(
                        id = "4",
                        name = "Bawang Merah",
                        quantity = 500,
                        unit = "gram",
                        location = "Rak Bumbu",
                        category = "Bumbu",
                        icon = Icons.Default.Grain,
                        expiryStatus = ExpiryStatus.Safe("Aman (15 hari)")
                    )
                )
            )
        )
    }
}

