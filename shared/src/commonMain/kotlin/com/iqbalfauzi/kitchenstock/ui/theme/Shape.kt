package com.iqbalfauzi.kitchenstock.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Shapes aligned with DESIGN.md
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Design Tokens (DESIGN.md)
val ShapeSm = RoundedCornerShape(8.dp)
val ShapeMd = RoundedCornerShape(14.dp)
val ShapeLg = RoundedCornerShape(20.dp)
val ShapeXl = RoundedCornerShape(24.dp)
val ShapeFull = CircleShape

object KitchenStockShapes {
    val sm = ShapeSm
    val md = ShapeMd
    val lg = ShapeLg
    val xl = ShapeXl
    val full = ShapeFull
}

// Convenient aliases & backwards compatibility
val ExtraSmallShape = ShapeSm
val DefaultShape = ShapeSm
val MediumShape = ShapeMd
val LargeShape = ShapeLg
val ExtraLargeShape = ShapeXl
val FullShape = ShapeFull
