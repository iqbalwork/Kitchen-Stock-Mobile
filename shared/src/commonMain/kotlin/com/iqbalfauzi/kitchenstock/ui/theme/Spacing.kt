package com.iqbalfauzi.kitchenstock.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class KitchenStockSpacing(
    val base: Dp = 4.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val marginMobile: Dp = 16.dp,
    val gutterMobile: Dp = 12.dp
)

// Design Tokens (DESIGN.md)
val SpacingXs = 4.dp
val SpacingSm = 8.dp
val SpacingMd = 12.dp
val SpacingLg = 16.dp
val SpacingXl = 24.dp

val LocalSpacing = staticCompositionLocalOf { KitchenStockSpacing() }
