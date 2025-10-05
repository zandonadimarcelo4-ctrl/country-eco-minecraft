package com.brazileconomy.mod.economy

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class Investment(
    var amount: Double,
    var rate: Double,
    var start: Long = Instant.now().epochSecond,
    var lastUpdate: Long = Instant.now().epochSecond
) {
    fun getDaysInvested(): Long {
        val now = Instant.now().epochSecond
        return ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(now))
    }
    
    fun getCurrentValue(): Double {
        val now = Instant.now().epochSecond
        val days = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(now))
        return amount * Math.pow(1 + rate, days.toDouble() / 30.0)
    }
}

fun updateInvestments(account: PlayerAccount) {
    val now = Instant.now().epochSecond
    account.investments.forEach {
        val days = ChronoUnit.DAYS.between(Instant.ofEpochSecond(it.lastUpdate), Instant.ofEpochSecond(now))
        if (days > 0) {
            it.amount *= Math.pow(1 + it.rate, days.toDouble() / 30.0)
            it.lastUpdate = now
        }
    }
}
