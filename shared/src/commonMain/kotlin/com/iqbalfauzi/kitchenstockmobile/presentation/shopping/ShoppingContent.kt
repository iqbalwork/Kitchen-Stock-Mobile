package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme

@Composable
fun ShoppingContent() {
    KitchenStockTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Shopping Screen")
        }
    }
}

@Preview
@Composable
fun ShoppingContentPreview() {
    ShoppingContent()
}
