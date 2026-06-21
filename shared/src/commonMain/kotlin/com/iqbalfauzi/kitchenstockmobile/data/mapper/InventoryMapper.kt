package com.iqbalfauzi.kitchenstockmobile.data.mapper

import com.iqbalfauzi.kitchenstockmobile.data.remote.model.InventoryDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.ProductDto
import com.iqbalfauzi.kitchenstockmobile.db.GetInventoryItemById
import com.iqbalfauzi.kitchenstockmobile.db.GetInventoryWithDetails
import com.iqbalfauzi.kitchenstockmobile.db.Product as ProductEntity
import com.iqbalfauzi.kitchenstockmobile.db.StorageLocation as StorageLocationEntity
import com.iqbalfauzi.kitchenstockmobile.domain.model.Category
import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.model.StorageLocation
import kotlin.time.Instant
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
