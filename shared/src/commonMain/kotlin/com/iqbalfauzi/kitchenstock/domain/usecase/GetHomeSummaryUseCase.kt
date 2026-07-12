package com.iqbalfauzi.kitchenstock.domain.usecase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Kitchen
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstock.presentation.home.model.AttentionItem
import com.iqbalfauzi.kitchenstock.presentation.home.model.AttentionStatus
import com.iqbalfauzi.kitchenstock.presentation.home.model.HomeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GetHomeSummaryUseCase(
    private val repository: InventoryRepository
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<HomeUiState> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val threeDaysLater = today.plus(3, DateTimeUnit.DAY)

        return combine(
            repository.getInventoryItems(),
            repository.getProducts()
        ) { items, products ->
            val expiringItems = items.filter { 
                it.expiryDate != null && it.expiryDate <= threeDaysLater && it.quantity > 0 
            }
            val outOfStockItems = items.filter { it.quantity <= 0 }
            
            // Map to presentation model
            val attentionItems = mutableListOf<AttentionItem>()
            
            expiringItems.forEach { 
                val days = today.daysUntil(it.expiryDate!!)
                val label = when {
                    days <= 0 -> "Expired"
                    days == 1 -> "Exp. Tomorrow"
                    else -> "Exp. in $days Days"
                }
                attentionItems.add(
                    AttentionItem(
                        id = it.id,
                        name = it.product?.name ?: "Unknown",
                        detail = "${it.quantity} ${it.product?.unit} remaining",
                        status = AttentionStatus.Expiring(label),
                        icon = Icons.Default.Kitchen
                    )
                )
            }
            
            outOfStockItems.forEach {
                attentionItems.add(
                    AttentionItem(
                        id = it.id,
                        name = it.product?.name ?: "Unknown",
                        detail = "0 ${it.product?.unit} remaining",
                        status = AttentionStatus.OutOfStock(),
                        icon = Icons.Default.ErrorOutline
                    )
                )
            }

            HomeUiState(
                totalItems = items.size,
                expiringCount = expiringItems.size,
                outOfStockCount = outOfStockItems.size,
                attentionItems = attentionItems.take(10), // Limit to top 10
                products = products
            )
        }
    }
}
