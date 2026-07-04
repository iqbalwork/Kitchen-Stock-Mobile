package com.iqbalfauzi.kitchenstockmobile.data.mapper

import com.iqbalfauzi.kitchenstockmobile.data.remote.model.ShoppingListDto
import com.iqbalfauzi.kitchenstockmobile.db.GetShoppingListWithDetails
import com.iqbalfauzi.kitchenstockmobile.domain.model.ShoppingListItem
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.model.Category
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

fun ShoppingListItem.toDto(userId: String): ShoppingListDto {
    return ShoppingListDto(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        isBought = isBought,
        createdAt = createdAt?.toString(),
        updatedAt = updatedAt?.toString()
    )
}
