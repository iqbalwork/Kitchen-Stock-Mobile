package com.iqbalfauzi.kitchenstock.presentation.shopping

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqbalfauzi.kitchenstock.presentation.shopping.model.ShoppingBadgeType
import com.iqbalfauzi.kitchenstock.presentation.shopping.model.ShoppingItem
import com.iqbalfauzi.kitchenstock.presentation.shopping.model.ShoppingUiState
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
fun ShoppingContent(
    uiState: ShoppingUiState,
    onIntent: (ShoppingIntent) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onIntent(ShoppingIntent.DismissUserMessage)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ShoppingHeaderBar(
                pendingCount = uiState.pendingCount,
                onClearAllBought = { onIntent(ShoppingIntent.ClearAllBought) },
                onCompleteShopping = { onIntent(ShoppingIntent.CompleteShopping) },
                onAddManual = onAddClick
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { onIntent(ShoppingIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = spacing.md,
                    end = spacing.md,
                    top = spacing.sm,
                    bottom = spacing.xl
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // 1. Quick Add Bar
                item(key = "quick_add_bar") {
                    QuickAddBar(
                        input = uiState.quickAddInput,
                        onInputChange = { onIntent(ShoppingIntent.UpdateQuickAddText(it)) },
                        onQuickAdd = { onIntent(ShoppingIntent.QuickAddCurrentItem) }
                    )
                }

                // 2. Section "Perlu Dibeli" Header
                item(key = "header_pending") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Perlu Dibeli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = "${uiState.pendingCount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Urut berdasarkan urgensi",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Empty state for "Perlu Dibeli"
                if (uiState.pendingItems.isEmpty()) {
                    item(key = "empty_pending") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = KitchenStockShapes.lg,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusSafe,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.sm))
                                Text(
                                    text = if (uiState.boughtItems.isNotEmpty()) {
                                        "Semua belanjaan sudah dicentang!"
                                    } else {
                                        "Daftar belanja masih kosong."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.pendingItems, key = { it.id }) { item ->
                        PendingShoppingCard(
                            item = item,
                            onToggle = { onIntent(ShoppingIntent.ToggleItem(item.id)) },
                            onDelete = { onIntent(ShoppingIntent.DeleteItem(item.id)) }
                        )
                    }
                }

                // 3. Section "Sudah Dibeli" Header
                item(key = "header_bought") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(KitchenStockShapes.sm)
                            .clickable { onIntent(ShoppingIntent.ToggleBoughtExpanded) }
                            .padding(vertical = spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.isBoughtExpanded) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.AutoMirrored.Filled.KeyboardArrowRight
                            },
                            contentDescription = if (uiState.isBoughtExpanded) "Tutup" else "Buka",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = "Sudah Dibeli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = "${uiState.boughtCount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Selesai",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Items in "Sudah Dibeli"
                if (uiState.isBoughtExpanded && uiState.boughtItems.isNotEmpty()) {
                    items(uiState.boughtItems, key = { it.id }) { item ->
                        BoughtShoppingCard(
                            item = item,
                            onToggle = { onIntent(ShoppingIntent.ToggleItem(item.id)) },
                            onUndo = { onIntent(ShoppingIntent.ToggleItem(item.id)) }
                        )
                    }
                }

                // 4. Auto-Restock Banner
                item(key = "auto_restock_banner") {
                    AutoRestockBanner(
                        boughtCount = uiState.boughtCount,
                        onCompleteShopping = { onIntent(ShoppingIntent.CompleteShopping) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingHeaderBar(
    pendingCount: Int,
    onClearAllBought: () -> Unit = {},
    onCompleteShopping: () -> Unit = {},
    onAddManual: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md)
            .padding(top = spacing.sm, bottom = spacing.xs)
    ) {
        // Status Row (Offline Chip & Local Storage Indicator)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
                        text = "Mode Offline Aktif",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Penyimpanan Lokal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(StatusSafe, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.sm))

        // Title and Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daftar Belanja",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(SecondaryLight, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$pendingCount item perlu dibeli",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Broom icon button to clear bought items (touch target min 48dp)
                IconButton(
                    onClick = onClearAllBought,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Bersihkan yang sudah dibeli",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 3-dots more menu (touch target min 48dp)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opsi Lainnya",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Selesaikan Belanja (Auto-Restock)") },
                            onClick = {
                                showMenu = false
                                onCompleteShopping()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bersihkan Item Sudah Dibeli") },
                            onClick = {
                                showMenu = false
                                onClearAllBought()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tambah Belanjaan Manual") },
                            onClick = {
                                showMenu = false
                                onAddManual()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAddBar(
    input: String,
    onInputChange: (String) -> Unit,
    onQuickAdd: () -> Unit
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = KitchenStockShapes.full,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = spacing.md, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(spacing.sm))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (input.isEmpty()) {
                    Text(
                        text = "Tambah belanjaan baru...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                BasicTextField(
                    value = input,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(PrimaryLight),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onQuickAdd() })
                )
            }

            Spacer(modifier = Modifier.width(spacing.xs))

            // 48dp Touch Target container with 44dp circular button
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onQuickAdd() },
                    shape = CircleShape,
                    color = PrimaryLight
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah belanjaan",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingShoppingCard(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = KitchenStockShapes.lg,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox touch target min 48×48 dp
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryLight,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = spacing.xs)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (item.badgeText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ShoppingSourceBadge(
                        text = item.badgeText,
                        type = item.badgeType
                    )
                }
            }

            // Delete action touch target min 48×48 dp
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Hapus item",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun BoughtShoppingCard(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onUndo: () -> Unit
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = KitchenStockShapes.lg,
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checked Checkbox min 48×48 dp
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = true,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryLight,
                        checkmarkColor = Color.White
                    )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = spacing.xs)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))
                ShoppingSourceBadge(
                    text = item.badgeText ?: "Restock otomatis ke Pantry",
                    type = ShoppingBadgeType.Restock
                )
            }

            // Undo action min 48×48 dp
            IconButton(
                onClick = onUndo,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Batalkan centang",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ShoppingSourceBadge(
    text: String,
    type: ShoppingBadgeType
) {
    when (type) {
        ShoppingBadgeType.Danger -> {
            Surface(
                shape = KitchenStockShapes.sm,
                color = StatusDangerBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(StatusDanger, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusDanger
                    )
                }
            }
        }
        ShoppingBadgeType.Warning -> {
            Surface(
                shape = KitchenStockShapes.sm,
                color = StatusWarningBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(StatusWarning, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusWarning
                    )
                }
            }
        }
        ShoppingBadgeType.Neutral -> {
            Surface(
                shape = KitchenStockShapes.sm,
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Article,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        ShoppingBadgeType.Restock -> {
            Surface(
                shape = KitchenStockShapes.sm,
                color = StatusSafeBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = StatusSafe
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusSafe
                    )
                }
            }
        }
    }
}

@Composable
fun AutoRestockBanner(
    boughtCount: Int,
    onCompleteShopping: () -> Unit
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = KitchenStockShapes.lg,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(
                    text = "Centang item untuk otomatis restock ke stok dapur saat belanja selesai.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(spacing.md))

            Button(
                onClick = onCompleteShopping,
                enabled = boughtCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = KitchenStockShapes.md,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLight,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = "Selesaikan Belanja ($boughtCount item)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// PREVIEWS (Mandatory Standards: Light & Dark)
// ==========================================

private val samplePendingItems = listOf(
    ShoppingItem(
        id = "1",
        name = "Susu UHT Full Cream",
        quantity = "1 liter",
        isChecked = false,
        badgeText = "Stok Habis di Kulkas",
        badgeType = ShoppingBadgeType.Danger
    ),
    ShoppingItem(
        id = "2",
        name = "Dada Ayam Fillet",
        quantity = "1 kg",
        isChecked = false,
        badgeText = "Stok Menipis di Freezer",
        badgeType = ShoppingBadgeType.Warning
    ),
    ShoppingItem(
        id = "3",
        name = "Bawang Putih",
        quantity = "250 gram",
        isChecked = false,
        badgeText = "Bumbu Dapur",
        badgeType = ShoppingBadgeType.Neutral
    ),
    ShoppingItem(
        id = "4",
        name = "Kecap Manis Refill",
        quantity = "1 pcs",
        isChecked = false,
        badgeText = "Pantry Utama",
        badgeType = ShoppingBadgeType.Neutral
    )
)

private val sampleBoughtItems = listOf(
    ShoppingItem(
        id = "5",
        name = "Minyak Goreng 2L",
        quantity = "1 pouch",
        isChecked = true,
        badgeText = "Restock otomatis ke Pantry",
        badgeType = ShoppingBadgeType.Restock
    ),
    ShoppingItem(
        id = "6",
        name = "Garam Dapur 500g",
        quantity = "1 bungkus",
        isChecked = true,
        badgeText = "Restock otomatis ke Pantry",
        badgeType = ShoppingBadgeType.Restock
    )
)

@Preview
@Composable
fun ShoppingContentLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        ShoppingContent(
            uiState = ShoppingUiState(
                quickAddInput = "",
                pendingItems = samplePendingItems,
                boughtItems = sampleBoughtItems,
                isBoughtExpanded = true
            )
        )
    }
}

@Preview
@Composable
fun ShoppingContentDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        ShoppingContent(
            uiState = ShoppingUiState(
                quickAddInput = "",
                pendingItems = samplePendingItems,
                boughtItems = sampleBoughtItems,
                isBoughtExpanded = true
            )
        )
    }
}

@Preview
@Composable
fun ShoppingHeaderBarLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        ShoppingHeaderBar(pendingCount = 4)
    }
}

@Preview
@Composable
fun ShoppingHeaderBarDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        ShoppingHeaderBar(pendingCount = 4)
    }
}

@Preview
@Composable
fun QuickAddBarLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        QuickAddBar(
            input = "Telur Ayam 1kg",
            onInputChange = {},
            onQuickAdd = {}
        )
    }
}

@Preview
@Composable
fun QuickAddBarDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        QuickAddBar(
            input = "Telur Ayam 1kg",
            onInputChange = {},
            onQuickAdd = {}
        )
    }
}

@Preview
@Composable
fun PendingShoppingCardLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        PendingShoppingCard(
            item = samplePendingItems[0],
            onToggle = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
fun PendingShoppingCardDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        PendingShoppingCard(
            item = samplePendingItems[0],
            onToggle = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
fun BoughtShoppingCardLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        BoughtShoppingCard(
            item = sampleBoughtItems[0],
            onToggle = {},
            onUndo = {}
        )
    }
}

@Preview
@Composable
fun BoughtShoppingCardDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        BoughtShoppingCard(
            item = sampleBoughtItems[0],
            onToggle = {},
            onUndo = {}
        )
    }
}

@Preview
@Composable
fun AutoRestockBannerLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        AutoRestockBanner(
            boughtCount = 2,
            onCompleteShopping = {}
        )
    }
}

@Preview
@Composable
fun AutoRestockBannerDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        AutoRestockBanner(
            boughtCount = 2,
            onCompleteShopping = {}
        )
    }
}
