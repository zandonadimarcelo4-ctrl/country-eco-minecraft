package com.countrymod.manager;

import com.countrymod.economy.*;
import com.countrymod.util.CPFGenerator;
import java.util.*;

/**
 * Manages all player economic accounts and transactions.
 * Integrated with the country system for economic operations.
 */
public class EconomyManager {
	private Map<UUID, PlayerAccount> accounts;
	
	public EconomyManager() {
		this.accounts = new HashMap<>();
	}
	
	public PlayerAccount getOrCreateAccount(UUID playerUuid, String playerName) {
		return accounts.computeIfAbsent(playerUuid, uuid -> {
			PlayerAccount account = new PlayerAccount(uuid, playerName);
			// Auto-generate CPF for new accounts
			account.setCpf(CPFGenerator.generateCPF());
			return account;
		});
	}
	
	public PlayerAccount getAccount(UUID playerUuid) {
		return accounts.get(playerUuid);
	}
	
	public boolean hasAccount(UUID playerUuid) {
		return accounts.containsKey(playerUuid);
	}
	
	public Collection<PlayerAccount> getAllAccounts() {
		return accounts.values();
	}
	
	public boolean transferMoney(UUID fromUuid, UUID toUuid, double amount) {
		PlayerAccount fromAccount = accounts.get(fromUuid);
		PlayerAccount toAccount = accounts.get(toUuid);
		
		if (fromAccount == null || toAccount == null || amount <= 0) {
			return false;
		}
		
		if (fromAccount.removeBalance(amount)) {
			toAccount.addBalance(amount);
			
			// Record transactions
			Transaction sendTransaction = new Transaction(
				Transaction.TransactionType.PIX_SEND,
				fromAccount.getPlayerName(),
				toAccount.getPlayerName(),
				amount,
				"PIX sent to " + toAccount.getPlayerName()
			);
			fromAccount.addTransaction(sendTransaction);
			
			Transaction receiveTransaction = new Transaction(
				Transaction.TransactionType.PIX_RECEIVE,
				fromAccount.getPlayerName(),
				toAccount.getPlayerName(),
				amount,
				"PIX received from " + fromAccount.getPlayerName()
			);
			toAccount.addTransaction(receiveTransaction);
			
			return true;
		}
		
		return false;
	}
	
	public void updateAllAccounts() {
		for (PlayerAccount account : accounts.values()) {
			// Apply loan interest
			for (Loan loan : account.getLoans()) {
				loan.applyInterest();
			}
			
			// Update investments
			for (Investment investment : account.getInvestments()) {
				investment.updateValue();
			}
			
			// Update credit score
			ScoreManager.updateScore(account);
		}
	}
	
	public Map<UUID, PlayerAccount> getAccountsMap() {
		return accounts;
	}
	
	public void setAccountsMap(Map<UUID, PlayerAccount> accounts) {
		this.accounts = accounts;
	}
}
