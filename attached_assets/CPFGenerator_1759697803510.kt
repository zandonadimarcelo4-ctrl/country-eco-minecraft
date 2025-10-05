package com.brazileconomy.mod.util

object CPFGenerator {
    fun generateCPF(): String {
        val base = (1..9).map { (0..9).random() }.joinToString("")
        val d1 = calculateDigit(base, weights = (10 downTo 2).toList())
        val d2 = calculateDigit(base + d1, weights = (11 downTo 2).toList())
        val cpf = base + d1 + d2
        return CPFValidator.formatCPF(cpf)
    }

    private fun calculateDigit(numbers: String, weights: List<Int>): String {
        val sum = numbers.mapIndexed { i, c -> c.digitToInt() * weights[i] }.sum()
        val res = sum % 11
        return if (res < 2) "0" else (11 - res).toString()
    }
}
