package com.countrymod.economy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a financial transaction between players or with the system.
 */
public class Transaction {
        private String transactionId;
        private TransactionType type;
        private String fromPlayer;
        private String toPlayer;
        private double amount;
        private String timestamp;
        private String description;
        
        public enum TransactionType {
                PIX_SEND,
                PIX_RECEIVE,
                ADMIN_ADD,
                ADMIN_REMOVE,
                SHOP_PURCHASE,
                SHOP_SALE,
                LOAN_TAKEN,
                LOAN_PAID,
                INVESTMENT_MADE,
                INVESTMENT_RETURN,
                TAX_PAID,
                SALARY_RECEIVED
        }
        
        public Transaction(TransactionType type, String fromPlayer, String toPlayer, double amount, String description) {
                this.transactionId = UUID.randomUUID().toString();
                this.type = type;
                this.fromPlayer = fromPlayer;
                this.toPlayer = toPlayer;
                this.amount = amount;
                this.timestamp = LocalDateTime.now().toString();
                this.description = description;
        }
        
        public String getTransactionId() {
                return transactionId;
        }
        
        public TransactionType getType() {
                return type;
        }
        
        public String getFromPlayer() {
                return fromPlayer;
        }
        
        public String getToPlayer() {
                return toPlayer;
        }
        
        public double getAmount() {
                return amount;
        }
        
        public String getTimestamp() {
                return timestamp;
        }
        
        public String getDescription() {
                return description;
        }
        
        public String getFormattedTimestamp() {
                try {
                        LocalDateTime dateTime = LocalDateTime.parse(timestamp);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        return dateTime.format(formatter);
                } catch (Exception e) {
                        return timestamp;
                }
        }
        
        @Override
        public String toString() {
                return String.format("[%s] %s: REI$ %.2f - %s", getFormattedTimestamp(), type, amount, description);
        }
        
        public String getFormattedAmount() {
                return String.format("REI$ %.2f", amount);
        }
}
