package com.countrymod.manager;

import com.countrymod.economy.*;
import com.countrymod.util.CPFGenerator;
import java.util.*;

/**
 * Gerencia todas as contas econômicas dos jogadores e transações.
 * Integrado com o sistema de países para operações econômicas.
 */
public class EconomyManager {
    private Map<UUID, PlayerAccount> accounts;

    public EconomyManager() {
        this.accounts = new HashMap<>();
    }

    /**
     * Retorna a conta do jogador ou cria uma nova se não existir.
     */
    public PlayerAccount getOrCreateAccount(UUID playerUuid, String playerName) {
        return accounts.computeIfAbsent(playerUuid, uuid -> {
            PlayerAccount account = new PlayerAccount(uuid, playerName);
            account.setCpf(CPFGenerator.generateCPF()); // Gera CPF automático
            return account;
        });
    }

    /**
     * Retorna a conta do jogador, ou null se não existir.
     */
    public PlayerAccount getAccount(UUID playerUuid) {
        return accounts.get(playerUuid);
    }

    /**
     * Verifica se o jogador possui conta.
     */
    public boolean hasAccount(UUID playerUuid) {
        return accounts.containsKey(playerUuid);
    }

    /**
     * Retorna todas as contas.
     */
    public Collection<PlayerAccount> getAllAccounts() {
        return accounts.values();
    }

    /**
     * Transfere dinheiro de uma conta para outra.
     */
    public boolean transferMoney(UUID fromUuid, UUID toUuid, double amount) {
        PlayerAccount fromAccount = accounts.get(fromUuid);
        PlayerAccount toAccount = accounts.get(toUuid);

        if (fromAccount == null || toAccount == null || amount <= 0) {
            return false;
        }

        if (fromAccount.removeBalance(amount)) {
            toAccount.addBalance(amount);

            // Registrar transações
            Transaction sendTransaction = new Transaction(
                Transaction.TransactionType.PIX_SEND,
                fromAccount.getPlayerName(),
                toAccount.getPlayerName(),
                amount,
                "PIX enviado para " + toAccount.getPlayerName()
            );
            fromAccount.addTransaction(sendTransaction);

            Transaction receiveTransaction = new Transaction(
                Transaction.TransactionType.PIX_RECEIVE,
                fromAccount.getPlayerName(),
                toAccount.getPlayerName(),
                amount,
                "PIX recebido de " + fromAccount.getPlayerName()
            );
            toAccount.addTransaction(receiveTransaction);

            return true;
        }

        return false;
    }

    /**
     * Atualiza todas as contas: juros de empréstimos, investimentos e score.
     */
    public void updateAllAccounts() {
        for (PlayerAccount account : accounts.values()) {
            // Aplicar juros de empréstimos
            for (Loan loan : account.getLoans()) {
                loan.applyInterest();
            }

            // Atualizar investimentos
            for (Investment investment : account.getInvestments()) {
                investment.updateValue();
            }

            // Atualizar score de crédito
            ScoreManager.updateScore(account);
        }
    }

    /**
     * Retorna o saldo do jogador, criando a conta se não existir.
     */
    public double getBalance(UUID playerUuid) {
        PlayerAccount account = getOrCreateAccount(playerUuid, "Unknown");
        return account.getBalance();
    }

    /**
     * Retorna o score de crédito do jogador, criando a conta se não existir.
     */
    public int getCreditScore(UUID playerUuid) {
        PlayerAccount account = getOrCreateAccount(playerUuid, "Unknown");
        return account.getCreditScore();
    }

    /**
     * Retorna o mapa de contas completo (para persistência ou debug).
     */
    public Map<UUID, PlayerAccount> getAccountsMap() {
        return accounts;
    }

    /**
     * Define o mapa de contas (para carregar dados salvos).
     */
    public void setAccountsMap(Map<UUID, PlayerAccount> accounts) {
        this.accounts = accounts;
    }
}
