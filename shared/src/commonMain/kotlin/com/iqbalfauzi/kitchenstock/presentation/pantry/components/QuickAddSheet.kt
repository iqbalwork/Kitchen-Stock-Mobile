package com.iqbalfauzi.kitchenstock.presentation.pantry.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockShapes
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockTheme
import com.iqbalfauzi.kitchenstock.ui.theme.LocalSpacing
import com.iqbalfauzi.kitchenstock.ui.theme.PrimaryLight
import com.iqbalfauzi.kitchenstock.ui.theme.StatusDanger
import com.iqbalfauzi.kitchenstock.ui.theme.StatusSafe
import com.iqbalfauzi.kitchenstock.ui.theme.StatusSafeBg
import com.iqbalfauzi.kitchenstock.ui.theme.StatusWarning
import com.iqbalfauzi.kitchenstock.ui.theme.StatusWarningBg
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

enum class ExpiryPreset(val label: String) {
    PLUS_3_DAYS("+3 Hari"),
    PLUS_7_DAYS("+7 Hari"),
    PLUS_1_MONTH("+1 Bulan"),
    MANUAL("Manual")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    isVisible: Boolean,
    storageLocations: List<StorageLocation>,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onQuickAdd: (
        name: String,
        quantity: Double,
        unit: String,
        storageLocationId: String,
        expiryDate: LocalDate?,
        categoryId: String?,
        keepOpen: Boolean
    ) -> Unit
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = KitchenStockShapes.xl,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        QuickAddSheetContent(
            storageLocations = storageLocations,
            categories = categories,
            onDismiss = onDismiss,
            onQuickAdd = onQuickAdd
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickAddSheetContent(
    storageLocations: List<StorageLocation>,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onQuickAdd: (
        name: String,
        quantity: Double,
        unit: String,
        storageLocationId: String,
        expiryDate: LocalDate?,
        categoryId: String?,
        keepOpen: Boolean
    ) -> Unit
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableDoubleStateOf(1.0) }
    var selectedUnit by remember { mutableStateOf("pcs") }
    var selectedLocationId by remember(storageLocations) {
        mutableStateOf(storageLocations.firstOrNull()?.id ?: "loc-kulkas")
    }
    var selectedPreset by remember { mutableStateOf(ExpiryPreset.PLUS_7_DAYS) }
    var manualExpiryDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val effectiveExpiryDate: LocalDate? = remember(selectedPreset, manualExpiryDate, today) {
        when (selectedPreset) {
            ExpiryPreset.PLUS_3_DAYS -> today.plus(DatePeriod(days = 3))
            ExpiryPreset.PLUS_7_DAYS -> today.plus(DatePeriod(days = 7))
            ExpiryPreset.PLUS_1_MONTH -> today.plus(DatePeriod(months = 1))
            ExpiryPreset.MANUAL -> manualExpiryDate
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = remember(manualExpiryDate) {
                manualExpiryDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC).date
                            manualExpiryDate = pickedDate
                            selectedPreset = ExpiryPreset.MANUAL
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight)
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md)
            .padding(bottom = spacing.lg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Tambah Bahan",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(spacing.sm))
                // Offline chip
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lokal Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Scrollable Form Body
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            // Section 1: Detail Bahan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = KitchenStockShapes.lg,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "Detail Bahan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "*Wajib",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusDanger,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(
                        text = "Nama Bahan / Komoditas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Misal: Dada Ayam Fillet, Telur...") },
                        singleLine = true,
                        shape = KitchenStockShapes.md,
                        trailingIcon = {
                            if (name.isNotEmpty()) {
                                IconButton(
                                    onClick = { name = "" },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus nama"
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // Suggestion Chips
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "⚡ Cepat:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        val suggestions = listOf("Telur", "Bawang Merah", "Susu UHT", "Minyak Goreng")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            suggestions.forEach { suggestion ->
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.clickable {
                                        name = suggestion
                                        when (suggestion) {
                                            "Telur" -> {
                                                selectedUnit = "pcs"
                                                selectedPreset = ExpiryPreset.PLUS_7_DAYS
                                                storageLocations.find { it.name.contains("Kulkas", ignoreCase = true) }?.let {
                                                    selectedLocationId = it.id
                                                }
                                            }
                                            "Bawang Merah" -> {
                                                selectedUnit = "gram"
                                                quantity = 250.0
                                                selectedPreset = ExpiryPreset.PLUS_1_MONTH
                                                storageLocations.find { it.name.contains("Bumbu", ignoreCase = true) || it.name.contains("Pantry", ignoreCase = true) }?.let {
                                                    selectedLocationId = it.id
                                                }
                                            }
                                            "Susu UHT" -> {
                                                selectedUnit = "liter"
                                                selectedPreset = ExpiryPreset.PLUS_7_DAYS
                                                storageLocations.find { it.name.contains("Kulkas", ignoreCase = true) }?.let {
                                                    selectedLocationId = it.id
                                                }
                                            }
                                            "Minyak Goreng" -> {
                                                selectedUnit = "liter"
                                                selectedPreset = ExpiryPreset.PLUS_1_MONTH
                                                storageLocations.find { it.name.contains("Pantry", ignoreCase = true) }?.let {
                                                    selectedLocationId = it.id
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Kuantitas & Satuan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = KitchenStockShapes.lg,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Scale,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(spacing.xs))
                                Text(
                                    text = "Kuantitas Stok",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Sesuaikan jumlah unit saat ini",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Stepper 48dp
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                IconButton(
                                    onClick = { if (quantity > 1.0) quantity -= 1.0 },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Kurang")
                                }
                                Text(
                                    text = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString(),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { quantity += 1.0 },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Tambah",
                                        tint = PrimaryLight
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.md))

                    Text(
                        text = "Pilih Satuan Pengukuran:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))

                    val units = listOf("bungkus", "kg", "gram", "pcs", "liter")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        units.forEach { unit ->
                            val isSelected = selectedUnit.equals(unit, ignoreCase = true)
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.surfaceContainer,
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { selectedUnit = unit }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = unit,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Lokasi Penyimpanan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = KitchenStockShapes.lg,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Kitchen,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = "Lokasi Penyimpanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.sm))

                    val displayLocations = remember(storageLocations) {
                        if (storageLocations.isNotEmpty()) {
                            storageLocations
                        } else {
                            listOf(
                                StorageLocation("loc-kulkas", "Kulkas", "Chiller"),
                                StorageLocation("loc-freezer", "Freezer", "Beku"),
                                StorageLocation("loc-bumbu", "Rak Bumbu", "Bumbu"),
                                StorageLocation("loc-lemari-pantry", "Pantry", "Suhu ruang")
                            )
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 2
                    ) {
                        displayLocations.forEach { location ->
                            val isSelected = selectedLocationId == location.id
                            val icon: ImageVector = when {
                                location.name.contains("Kulkas", ignoreCase = true) ||
                                    location.name.contains("Fridge", ignoreCase = true) -> Icons.Default.Kitchen
                                location.name.contains("Freezer", ignoreCase = true) -> Icons.Default.AcUnit
                                location.name.contains("Bumbu", ignoreCase = true) -> Icons.Default.Coffee
                                else -> Icons.Default.Inventory2
                            }

                            Surface(
                                shape = KitchenStockShapes.md,
                                color = if (isSelected) PrimaryLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceContainer,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { selectedLocationId = location.id }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = location.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = PrimaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Tanggal Kedaluwarsa
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = KitchenStockShapes.lg,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "Tanggal Kedaluwarsa",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Batas Konsumsi",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // 2x2 Preset Cards
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 2
                    ) {
                        ExpiryPreset.entries.forEach { preset ->
                            val isSelected = selectedPreset == preset
                            val (title, subtitle) = when (preset) {
                                ExpiryPreset.PLUS_3_DAYS -> {
                                    val date = today.plus(DatePeriod(days = 3))
                                    "+3 Hari" to formatDateShort(date)
                                }
                                ExpiryPreset.PLUS_7_DAYS -> {
                                    val date = today.plus(DatePeriod(days = 7))
                                    "+7 Hari" to formatDateShort(date)
                                }
                                ExpiryPreset.PLUS_1_MONTH -> {
                                    val date = today.plus(DatePeriod(months = 1))
                                    "+1 Bulan" to formatDateShort(date)
                                }
                                ExpiryPreset.MANUAL -> {
                                    "Manual" to (manualExpiryDate?.let { formatDateShort(it) } ?: "Pilih Kalender")
                                }
                            }

                            Surface(
                                shape = KitchenStockShapes.md,
                                color = if (isSelected) StatusWarningBg else MaterialTheme.colorScheme.surfaceContainer,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) StatusWarning else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clickable {
                                        selectedPreset = preset
                                        if (preset == ExpiryPreset.MANUAL) {
                                            showDatePicker = true
                                        }
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) StatusWarning else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(StatusWarning)
                                            )
                                        }
                                    }
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // Notice alert box
                    effectiveExpiryDate?.let { expiry ->
                        val daysRemaining = today.daysUntil(expiry)
                        val (bgColor, textColor, icon) = when {
                            daysRemaining <= 3 -> Triple(StatusWarningBg, StatusWarning, Icons.Default.HourglassEmpty)
                            daysRemaining <= 7 -> Triple(StatusWarningBg, StatusWarning, Icons.Default.Schedule)
                            else -> Triple(StatusSafeBg, StatusSafe, Icons.Default.Check)
                        }

                        Surface(
                            shape = KitchenStockShapes.sm,
                            color = bgColor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = textColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (daysRemaining <= 7) {
                                        "Mendekati Kedaluwarsa dalam $daysRemaining hari"
                                    } else {
                                        "Aman untuk stok bahan makanan ($daysRemaining hari lagi)"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Section 5: Kategori (Opsional)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = KitchenStockShapes.lg,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kategori",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Opsional",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.sm))

                    val sampleCategories = remember(categories) {
                        if (categories.isNotEmpty()) {
                            categories
                        } else {
                            listOf(
                                Category("cat-protein", "Daging & Unggas"),
                                Category("cat-sayur", "Sayur"),
                                Category("cat-bumbu", "Bumbu"),
                                Category("cat-minuman", "Dairy"),
                                Category("cat-lainnya", "Lainnya")
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleCategories.forEach { category ->
                            val isSelected = selectedCategoryId == category.id
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) PrimaryLight else MaterialTheme.colorScheme.surfaceContainer,
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .height(48.dp)
                                    .clickable {
                                        selectedCategoryId = if (isSelected) null else category.id
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Sticky Bottom Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            // Secondary Action: Simpan & Tambah Lagi
            OutlinedButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onQuickAdd(
                            name.trim(),
                            quantity,
                            selectedUnit,
                            selectedLocationId,
                            effectiveExpiryDate,
                            selectedCategoryId,
                            true
                        )
                        // Reset form fields
                        name = ""
                        quantity = 1.0
                        selectedUnit = "pcs"
                        selectedPreset = ExpiryPreset.PLUS_7_DAYS
                        manualExpiryDate = null
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = KitchenStockShapes.xl,
                border = BorderStroke(1.5.dp, PrimaryLight),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryLight
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Simpan & Tambah",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Primary Action: Simpan Bahan
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onQuickAdd(
                            name.trim(),
                            quantity,
                            selectedUnit,
                            selectedLocationId,
                            effectiveExpiryDate,
                            selectedCategoryId,
                            false
                        )
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = KitchenStockShapes.xl,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLight,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Simpan Bahan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

private fun formatDateShort(date: LocalDate): String {
    val monthName = when (date.month) {
        Month.JANUARY -> "Jan"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"
        Month.MAY -> "Mei"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Agu"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Okt"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Des"
    }
    return "${date.day} $monthName"
}



@Preview
@Composable
fun QuickAddSheetLightPreview() {
    KitchenStockTheme(darkTheme = false) {
        Surface {
            QuickAddSheetContent(
                storageLocations = listOf(
                    StorageLocation("1", "Kulkas"),
                    StorageLocation("2", "Freezer"),
                    StorageLocation("3", "Rak Bumbu"),
                    StorageLocation("4", "Pantry")
                ),
                categories = listOf(
                    Category("1", "Daging & Unggas"),
                    Category("2", "Sayur"),
                    Category("3", "Bumbu")
                ),
                onDismiss = {},
                onQuickAdd = { _, _, _, _, _, _, _ -> }
            )
        }
    }
}

@Preview
@Composable
fun QuickAddSheetDarkPreview() {
    KitchenStockTheme(darkTheme = true) {
        Surface {
            QuickAddSheetContent(
                storageLocations = listOf(
                    StorageLocation("1", "Kulkas"),
                    StorageLocation("2", "Freezer"),
                    StorageLocation("3", "Rak Bumbu"),
                    StorageLocation("4", "Pantry")
                ),
                categories = listOf(
                    Category("1", "Daging & Unggas"),
                    Category("2", "Sayur"),
                    Category("3", "Bumbu")
                ),
                onDismiss = {},
                onQuickAdd = { _, _, _, _, _, _, _ -> }
            )
        }
    }
}
