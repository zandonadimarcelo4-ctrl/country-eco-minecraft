package com.brazileconomy.mod.economy

object ScoreManager {
    fun updateScore(account: PlayerAccount) {
        var newScore = 600
        
        // Penaliza por empréstimos ativos
        val totalLoans = account.loans.sumOf { it.amount }
        if (totalLoans > 0) {
            newScore -= (totalLoans / 100).toInt().coerceAtMost(150)
        }
        
        // Penaliza por empréstimos vencidos
        if (account.loans.any { it.isOverdue() }) {
            newScore -= 100
        }
        
        // Bonifica por investimentos
        val totalInvestments = account.investments.sumOf { it.amount }
        if (totalInvestments > 0) {
            newScore += (totalInvestments / 200).toInt().coerceAtMost(100)
        }
        
        // Bonifica por saldo alto
        if (account.balance > 1000) {
            newScore += 50
        } else if (account.balance > 500) {
            newScore += 25
        }
        
        account.score = newScore.coerceIn(300, 850)
    }
    
    fun getScoreRating(score: Int): String {
        return when {
            score >= 800 -> "§a§lEXCELENTE"
            score >= 700 -> "§2§lMUITO BOM"
            score >= 600 -> "§e§lBOM"
            score >= 500 -> "§6§lREGULAR"
            score >= 400 -> "§c§lRUIM"
            else -> "§4§lMUITO RUIM"
        }
    }
}
