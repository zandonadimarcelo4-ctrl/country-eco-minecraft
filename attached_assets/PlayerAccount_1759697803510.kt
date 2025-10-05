package com.brazileconomy.mod.economy

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlayerAccount(
    val playerUUID: String,
    var playerName: String,
    var cpf: String? = null,
    var balance: Double = 100.0,
    val transactionHistory: MutableList<Transaction> = mutableListOf(),
    val loans: MutableList<Loan> = mutableListOf(),
    val investments: MutableList<Investment> = mutableListOf(),
    var score: Int = 600
) {
    
    fun hasCpf(): Boolean = cpf != null && cpf!!.isNotEmpty()
    
    fun addBalance(amount: Double) {
        balance += amount
    }
    
    fun removeBalance(amount: Double): Boolean {
        return if (balance >= amount) {
            balance -= amount
            true
        } else {
            false
        }
    }
    
    fun addTransaction(transaction: Transaction) {
        transactionHistory.add(transaction)
        if (transactionHistory.size > 50) {
            transactionHistory.removeAt(0)
        }
    }
    
    companion object {
        fun fromUUID(uuid: UUID, name: String): PlayerAccount {
            return PlayerAccount(uuid.toString(), name)
        }
    }
}
