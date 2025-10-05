package com.brazileconomy.mod.economy

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Transaction(
    val transactionId: String = java.util.UUID.randomUUID().toString(),
    val type: TransactionType,
    val fromPlayer: String,
    val toPlayer: String,
    val amount: Double,
    val timestamp: String = LocalDateTime.now().toString(),
    val description: String
) {
    
    @Serializable
    enum class TransactionType {
        PIX_SEND,
        PIX_RECEIVE,
        ADMIN_ADD,
        ADMIN_REMOVE,
        SHOP_PURCHASE,
        SHOP_SALE,
        LOAN_TAKEN,
        LOAN_PAID,
        INVESTMENT_MADE,
        INVESTMENT_RETURN
    }
    
    fun getFormattedTimestamp(): String {
        return try {
            val dateTime = LocalDateTime.parse(timestamp)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            dateTime.format(formatter)
        } catch (e: Exception) {
            timestamp
        }
    }
    
    override fun toString(): String {
        return "[${getFormattedTimestamp()}] $type: R$ ${"%.2f".format(amount)} - $description"
    }
}
