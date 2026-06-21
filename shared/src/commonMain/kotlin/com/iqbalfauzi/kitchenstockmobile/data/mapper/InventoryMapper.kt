package com.iqbalfauzi.kitchenstockmobile.data.mapper

import com.iqbalfauzi.kitchenstockmobile.db.GetInventoryWithDetails
import com.iqbalfauzi.kitchenstockmobile.domain.model.Category
import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.model.StorageLocation
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

fun GetInventoryWithDetails.toDomain(): InventoryItem {
    return InventoryItem(
        id = id,
        productId = productId,
        storageLocationId = storageLocationId,
        quantity = quantity,
        expiryDate = expiryDate?.let { LocalDate.parse(it) },
        updatedAt = Instant.parse(updatedAt),
        product = Product(
            id = productId,
            categoryId = productCategoryId,
            name = productName,
            unit = productUnit,
            imageUrl = productImageUrl,
            category = categoryName?.let {
                Category(id = productCategoryId ?: "", name = it, icon = categoryIcon)
            }
        ),
        location = StorageLocation(
            id = storageLocationId,
            name = locationName
        )
    )
}
