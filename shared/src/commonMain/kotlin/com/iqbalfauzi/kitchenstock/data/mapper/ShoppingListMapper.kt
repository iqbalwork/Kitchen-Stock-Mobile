package com.iqbalfauzi.kitchenstock.data.mapper

import com.iqbalfauzi.kitchenstock.db.GetShoppingListWithDetails
import com.iqbalfauzi.kitchenstock.domain.model.ShoppingListItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.Category
import kotlinx.datetime.Instant

fun GetShoppingListWithDetails.toDomain(): ShoppingListItem {
    return ShoppingListItem(
        id = id,
        productId = productId,
        quantity = quantity,
        isBought = isBought == 1L,
        createdAt = createdAt?.let { Instant.parse(it) },
        updatedAt = updatedAt?.let { Instant.parse(it) },
        product = Product(
            id = productId,
            categoryId = productCategoryId,
            name = productName,
            barcode = null,
            unit = productUnit,
            imageUrl = productImageUrl,
            category = categoryName?.let {
                Category(
                    id = productCategoryId ?: "",
                    name = it,
                    icon = categoryIcon
                )
            }
        )
    )
}
