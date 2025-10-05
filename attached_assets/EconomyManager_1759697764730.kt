package com.brazileconomy.mod.economy

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class EconomyManager {
    private val accounts: MutableMap<UUID, PlayerAccount> = mutableMapOf()
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val dataFile: Path = Paths.get("config", "brazileconomy", "economy_data.json")
    
    fun getOrCreateAccount(playerUUID: UUID, playerName: String): PlayerAccount {
        return accounts.getOrPut(playerUUID) { 
            PlayerAccount.fromUUID(playerUUID, playerName)
        }
    }
    
    fun getAccount(playerUUID: UUID): PlayerAccount? = accounts[playerUUID]
    
    fun hasAccount(playerUUID: UUID): Boolean = accounts.containsKey(playerUUID)
    
    fun saveData() {
        try {
            Files.createDirectories(dataFile.parent)
            
            val accountsList = accounts.values.toList()
            FileWriter(dataFile.toFile()).use { writer ->
                writer.write(json.encodeToString(accountsList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun loadData() {
        if (!Files.exists(dataFile)) return
        
        try {
            FileReader(dataFile.toFile()).use { reader ->
                val accountsList: List<PlayerAccount> = json.decodeFromString(reader.readText())
                accountsList.forEach { account ->
                    accounts[UUID.fromString(account.playerUUID)] = account
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun transferMoney(fromUUID: UUID, toUUID: UUID, amount: Double): Boolean {
        val fromAccount = accounts[fromUUID] ?: return false
        val toAccount = accounts[toUUID] ?: return false
        
        if (amount <= 0) return false
        
        if (fromAccount.removeBalance(amount)) {
            toAccount.addBalance(amount)
            
            val sendTransaction = Transaction(
                type = Transaction.TransactionType.PIX_SEND,
                fromPlayer = fromAccount.playerName,
                toPlayer = toAccount.playerName,
                amount = amount,
                description = "PIX enviado para ${toAccount.playerName}"
            )
            fromAccount.addTransaction(sendTransaction)
            
            val receiveTransaction = Transaction(
                type = Transaction.TransactionType.PIX_RECEIVE,
                fromPlayer = fromAccount.playerName,
                toPlayer = toAccount.playerName,
                amount = amount,
                description = "PIX recebido de ${fromAccount.playerName}"
            )
            toAccount.addTransaction(receiveTransaction)
            
            return true
        }
        
        return false
    }
    
    fun updateAllAccounts() {
        accounts.values.forEach { account ->
            applyLoanInterest(account)
            updateInvestments(account)
            ScoreManager.updateScore(account)
        }
        saveData()
    }
    
    fun getAllAccounts(): Collection<PlayerAccount> = accounts.values
}
