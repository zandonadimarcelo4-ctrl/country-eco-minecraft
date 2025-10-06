package com.countrymod.system;

import com.countrymod.CountryMod;
import com.countrymod.economy.Loan;
import com.countrymod.economy.PlayerAccount;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Advanced banking system with loans, credit scores, and financial services
 */
public class BankingSystem {
    private static final double BASE_INTEREST_RATE = 0.05; // 5% base interest
    private static final double MIN_CREDIT_SCORE = 300;
    private static final double MAX_CREDIT_SCORE = 850;
    
    /**
     * Apply for a loan with credit check
     */
    public boolean applyForLoan(ServerPlayerEntity player, double amount, int durationDays) {
        if (amount <= 0 || durationDays <= 0) {
            player.sendMessage(Text.literal("§c[BANK] §fInvalid loan parameters!"), false);
            return false;
        }
        
        var economyManager = CountryMod.getEconomyManager();
        PlayerAccount account = economyManager.getAccount(player.getUuid());
        
        if (account == null) {
            player.sendMessage(Text.literal("§c[BANK] §fAccount not found!"), false);
            return false;
        }
        
        // Credit score check
        int creditScore = account.getCreditScore();
        if (creditScore < 400) {
            player.sendMessage(Text.literal("§c[BANK] §fLoan denied! Your credit score is too low: " + creditScore), false);
            return false;
        }
        
        // Calculate max loan based on credit score
        double maxLoan = calculateMaxLoan(creditScore, account.getBalance());
        if (amount > maxLoan) {
            player.sendMessage(Text.literal("§c[BANK] §fLoan amount too high! Maximum you can borrow: REI$ " + 
                String.format("%.2f", maxLoan)), false);
            return false;
        }
        
        // Check existing loans
        double totalExistingLoans = account.getTotalLoans();
        if (totalExistingLoans + amount > maxLoan * 1.5) {
            player.sendMessage(Text.literal("§c[BANK] §fYou have too many existing loans!"), false);
            return false;
        }
        
        // Calculate interest rate based on credit score
        double interestRate = calculateInterestRate(creditScore);
        double totalRepayment = amount * (1 + interestRate);
        
        // Create and add loan
        Loan loan = new Loan(amount, interestRate, durationDays);
        account.addLoan(loan);
        account.deposit(amount, "Bank Loan");
        
        player.sendMessage(Text.literal("§a§l[BANK] §aLoan approved!" +
            "\n§fAmount: REI$ " + String.format("%.2f", amount) +
            "\n§fInterest Rate: " + String.format("%.2f", interestRate * 100) + "%" +
            "\n§fDuration: " + durationDays + " days" +
            "\n§fTotal Repayment: REI$ " + String.format("%.2f", totalRepayment) +
            "\n§fDaily Payment: REI$ " + String.format("%.2f", totalRepayment / durationDays)), false);
        
        return true;
    }
    
    /**
     * Repay a loan
     */
    public boolean repayLoan(ServerPlayerEntity player, double amount) {
        var economyManager = CountryMod.getEconomyManager();
        PlayerAccount account = economyManager.getAccount(player.getUuid());
        
        if (account == null) {
            return false;
        }
        
        List<Loan> loans = account.getLoans();
        if (loans.isEmpty()) {
            player.sendMessage(Text.literal("§c[BANK] §fYou have no active loans!"), false);
            return false;
        }
        
        Loan oldestLoan = loans.get(0);
        double remaining = oldestLoan.getRemainingAmount();
        
        if (amount > account.getBalance()) {
            player.sendMessage(Text.literal("§c[BANK] §fInsufficient funds!"), false);
            return false;
        }
        
        double paymentAmount = Math.min(amount, remaining);
        
        if (account.withdraw(paymentAmount)) {
            oldestLoan.makePayment(paymentAmount);
            
            // Improve credit score for on-time payment
            improveCreditScore(account, 5);
            
            if (oldestLoan.isFullyPaid()) {
                account.removeLoan(oldestLoan);
                player.sendMessage(Text.literal("§a[BANK] §aLoan fully repaid! Credit score improved!"), false);
            } else {
                player.sendMessage(Text.literal("§a[BANK] §fPayment of REI$ " + String.format("%.2f", paymentAmount) + 
                    " received. Remaining: REI$ " + String.format("%.2f", oldestLoan.getRemainingAmount())), false);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Calculate maximum loan amount based on credit score and balance
     */
    private double calculateMaxLoan(int creditScore, double balance) {
        double scoreMultiplier = (creditScore - MIN_CREDIT_SCORE) / (MAX_CREDIT_SCORE - MIN_CREDIT_SCORE);
        double maxLoan = 1000 + (scoreMultiplier * 9000); // Range: 1000-10000
        return Math.max(maxLoan, balance * 2); // Can borrow at least 2x current balance
    }
    
    /**
     * Calculate interest rate based on credit score
     */
    private double calculateInterestRate(int creditScore) {
        if (creditScore >= 750) return 0.03; // 3% for excellent credit
        if (creditScore >= 650) return 0.05; // 5% for good credit
        if (creditScore >= 550) return 0.08; // 8% for fair credit
        return 0.12; // 12% for poor credit
    }
    
    /**
     * Improve credit score
     */
    public void improveCreditScore(PlayerAccount account, int points) {
        int newScore = Math.min((int) MAX_CREDIT_SCORE, account.getCreditScore() + points);
        account.setCreditScore(newScore);
    }
    
    /**
     * Reduce credit score (for missed payments, etc.)
     */
    public void reduceCreditScore(PlayerAccount account, int points) {
        int newScore = Math.max((int) MIN_CREDIT_SCORE, account.getCreditScore() - points);
        account.setCreditScore(newScore);
    }
    
    /**
     * Open a savings account with interest
     */
    public boolean openSavingsAccount(ServerPlayerEntity player, double initialDeposit) {
        if (initialDeposit < 100) {
            player.sendMessage(Text.literal("§c[BANK] §fMinimum deposit for savings account: REI$ 100"), false);
            return false;
        }
        
        var economyManager = CountryMod.getEconomyManager();
        PlayerAccount account = economyManager.getAccount(player.getUuid());
        
        if (account == null || account.getBalance() < initialDeposit) {
            player.sendMessage(Text.literal("§c[BANK] §fInsufficient funds!"), false);
            return false;
        }
        
        // For now, create as an investment
        if (account.withdraw(initialDeposit)) {
            var investment = new com.countrymod.economy.Investment(initialDeposit, 0.02); // 2% annual
            account.addInvestment(investment);
            
            player.sendMessage(Text.literal("§a[BANK] §aSavings account opened!" +
                "\n§fDeposit: REI$ " + String.format("%.2f", initialDeposit) +
                "\n§fAnnual Interest: 2%"), false);
            
            return true;
        }
        
        return false;
    }
}
