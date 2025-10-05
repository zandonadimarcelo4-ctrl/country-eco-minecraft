package com.brazileconomy.mod.util

object CPFValidator {
    
    fun isValidCPF(cpf: String?): Boolean {
        if (cpf == null) return false
        
        val cleanCpf = cpf.replace(Regex("[^0-9]"), "")
        
        if (cleanCpf.length != 11) return false
        
        if (cleanCpf.all { it == cleanCpf[0] }) return false
        
        return try {
            val digits = cleanCpf.map { it.digitToInt() }
            
            var sum = 0
            for (i in 0..8) {
                sum += digits[i] * (10 - i)
            }
            var firstVerifier = 11 - (sum % 11)
            if (firstVerifier >= 10) firstVerifier = 0
            
            if (digits[9] != firstVerifier) return false
            
            sum = 0
            for (i in 0..9) {
                sum += digits[i] * (11 - i)
            }
            var secondVerifier = 11 - (sum % 11)
            if (secondVerifier >= 10) secondVerifier = 0
            
            digits[10] == secondVerifier
        } catch (e: Exception) {
            false
        }
    }
    
    fun formatCPF(cpf: String): String {
        val cleanCpf = cpf.replace(Regex("[^0-9]"), "")
        return if (cleanCpf.length == 11) {
            "${cleanCpf.substring(0, 3)}.${cleanCpf.substring(3, 6)}.${cleanCpf.substring(6, 9)}-${cleanCpf.substring(9, 11)}"
        } else {
            cpf
        }
    }
}
