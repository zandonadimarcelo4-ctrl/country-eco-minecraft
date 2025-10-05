package com.brazileconomy.mod.economy

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class Loan(
    var amount: Double,
    var interestRate: Double = 0.3,
    var createdAt: Long = Instant.now().epochSecond,
    var lastPayment: Long = Instant.now().epochSecond,
    var durationDays: Int = 30
) {
    fun getDaysRemaining(): Long {
        val now = Instant.now().epochSecond
        val endDate = createdAt + (durationDays * 86400L)
        val remaining = ChronoUnit.DAYS.between(Instant.ofEpochSecond(now), Instant.ofEpochSecond(endDate))
        return maxOf(0, remaining)
    }
    
    fun isOverdue(): Boolean {
        return getDaysRemaining() == 0L && amount > 0
    }
}

fun applyLoanInterest(account: PlayerAccount) {
    account.loans.forEach { loan ->
        val now = Instant.now().epochSecond
        val days = ChronoUnit.DAYS.between(Instant.ofEpochSecond(loan.lastPayment), Instant.ofEpochSecond(now))
        if (days > 0) {
            loan.amount *= Math.pow(1 + loan.interestRate, days.toDouble() / 30.0)
            loan.lastPayment = now
        }
    }
}
