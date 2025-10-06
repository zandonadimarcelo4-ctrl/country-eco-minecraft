package com.countrymod.economy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a player's economic account with balance, CPF, transactions, loans, and investments.
 * Integrated with the country system for economic management.
 */
public class PlayerAccount {
        private UUID playerUuid;
        private String playerName;
        private String cpf; // Brazilian tax ID
        private double balance;
        private List<Transaction> transactionHistory;
        private List<Loan> loans;
        private List<Investment> investments;
        private int creditScore;
        
        public PlayerAccount(UUID playerUuid, String playerName) {
                this.playerUuid = playerUuid;
                this.playerName = playerName;
                this.cpf = null;
                this.balance = 100.0; // Starting balance
                this.transactionHistory = new ArrayList<>();
                this.loans = new ArrayList<>();
                this.investments = new ArrayList<>();
                this.creditScore = 600; // Default credit score
        }
        
        public UUID getPlayerUuid() {
                return playerUuid;
        }
        
        public String getPlayerName() {
                return playerName;
        }
        
        public void setPlayerName(String playerName) {
                this.playerName = playerName;
        }
        
        public String getCpf() {
                return cpf;
        }
        
        public void setCpf(String cpf) {
                this.cpf = cpf;
        }
        
        public boolean hasCpf() {
                return cpf != null && !cpf.isEmpty();
        }
        
        public double getBalance() {
                return balance;
        }
        
        public void addBalance(double amount) {
                this.balance += amount;
        }
        
        public void deposit(double amount, String reason) {
                this.balance += amount;
                addTransaction(new Transaction(Transaction.TransactionType.ADMIN_ADD, "SYSTEM", playerName, amount, reason));
        }
        
        public boolean removeBalance(double amount) {
                if (balance >= amount) {
                        balance -= amount;
                        return true;
                }
                return false;
        }
        
        public boolean withdraw(double amount) {
                if (balance >= amount) {
                        balance -= amount;
                        addTransaction(new Transaction(Transaction.TransactionType.ADMIN_REMOVE, playerName, "SYSTEM", amount, "Withdrawal"));
                        return true;
                }
                return false;
        }
        
        public List<Transaction> getTransactionHistory() {
                return transactionHistory;
        }
        
        public void addTransaction(Transaction transaction) {
                transactionHistory.add(transaction);
                // Keep only last 50 transactions
                if (transactionHistory.size() > 50) {
                        transactionHistory.remove(0);
                }
        }
        
        public List<Loan> getLoans() {
                return loans;
        }
        
        public void addLoan(Loan loan) {
                loans.add(loan);
        }
        
        public void removeLoan(Loan loan) {
                loans.remove(loan);
        }
        
        public List<Investment> getInvestments() {
                return investments;
        }
        
        public void addInvestment(Investment investment) {
                investments.add(investment);
        }
        
        public void removeInvestment(Investment investment) {
                investments.remove(investment);
        }
        
        public int getCreditScore() {
                return creditScore;
        }
        
        public void setCreditScore(int creditScore) {
                this.creditScore = creditScore;
        }
        
        public double getTotalLoans() {
                return loans.stream().mapToDouble(Loan::getAmount).sum();
        }
        
        public double getTotalInvestments() {
                return investments.stream().mapToDouble(Investment::getCurrentValue).sum();
        }
}
