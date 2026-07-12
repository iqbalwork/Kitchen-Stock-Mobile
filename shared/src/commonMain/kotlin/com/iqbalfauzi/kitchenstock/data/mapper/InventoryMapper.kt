package com.iqbalfauzi.kitchenstock.data.mapper

import com.iqbalfauzi.kitchenstock.data.remote.model.InventoryDto
import com.iqbalfauzi.kitchenstock.data.remote.model.ProductDto
import com.iqbalfauzi.kitchenstock.db.GetInventoryItemById
import com.iqbalfauzi.kitchenstock.db.GetInventoryWithDetails
import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import com.iqbalfauzi.kitchenstock.db.Category as CategoryEntity
import com.iqbalfauzi.kitchenstock.db.Product as ProductEntity
import com.iqbalfauzi.kitchenstock.db.StorageLocation as StorageLocationEntity

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

fun GetInventoryItemById.toDomain(): InventoryItem {
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

fun InventoryItem.toDto(): InventoryDto {
    return InventoryDto(
        id = id,
        productId = productId,
        storageLocationId = storageLocationId,
        quantity = quantity,
        expiryDate = expiryDate?.toString(),
        updatedAt = updatedAt.toString()
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        categoryId = categoryId,
        name = name,
        barcode = barcode,
        unit = unit,
        minStockLevel = minStockLevel,
        imageUrl = imageUrl
    )
}

fun StorageLocationEntity.toDomain(): StorageLocation {
    return StorageLocation(
        id = id,
        name = name,
        description = description
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon
    )
}

fun Product.toDto(userId: String? = null): ProductDto {
    return ProductDto(
        id = id,
        userId = userId,
        categoryId = categoryId,
        name = name,
        barcode = barcode,
        unit = unit,
        minStockLevel = minStockLevel,
        imageUrl = imageUrl
    )
}
