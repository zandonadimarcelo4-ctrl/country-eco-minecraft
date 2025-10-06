package com.countrymod.system;

import com.countrymod.CountryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Secure player-to-player trading with escrow, reputation, and anti-scam protections
 */
public class SecureTradeSystem {
    private final Map<UUID, SecureTrade> activeTrades = new HashMap<>();
    private final Map<UUID, PlayerReputation> reputations = new HashMap<>();
    private final Map<UUID, List<TradeHistory>> tradeHistories = new HashMap<>();
    
    public static class SecureTrade {
        private final UUID tradeId;
        private final UUID seller;
        private final UUID buyer;
        private final ItemStack item;
        private final double price;
        private final long createdAt;
        private TradeStatus status;
        private boolean sellerConfirmed;
        private boolean buyerConfirmed;
        private double escrowAmount;
        
        public SecureTrade(UUID seller, UUID buyer, ItemStack item, double price) {
            this.tradeId = UUID.randomUUID();
            this.seller = seller;
            this.buyer = buyer;
            this.item = item.copy();
            this.price = price;
            this.createdAt = System.currentTimeMillis();
            this.status = TradeStatus.PENDING;
            this.sellerConfirmed = false;
            this.buyerConfirmed = false;
            this.escrowAmount = 0;
        }
        
        public UUID getTradeId() { return tradeId; }
        public UUID getSeller() { return seller; }
        public UUID getBuyer() { return buyer; }
        public ItemStack getItem() { return item; }
        public double getPrice() { return price; }
        public TradeStatus getStatus() { return status; }
        public void setStatus(TradeStatus status) { this.status = status; }
        public boolean isSellerConfirmed() { return sellerConfirmed; }
        public boolean isBuyerConfirmed() { return buyerConfirmed; }
        public void setSellerConfirmed(boolean confirmed) { this.sellerConfirmed = confirmed; }
        public void setBuyerConfirmed(boolean confirmed) { this.buyerConfirmed = confirmed; }
        public double getEscrowAmount() { return escrowAmount; }
        public void setEscrowAmount(double amount) { this.escrowAmount = amount; }
    }
    
    public enum TradeStatus {
        PENDING,
        ESCROW_LOCKED,
        COMPLETED,
        DISPUTED,
        CANCELLED,
        REFUNDED
    }
    
    public static class PlayerReputation {
        private int successfulTrades;
        private int failedTrades;
        private int disputes;
        private double totalTradeValue;
        private List<String> reviews;
        
        public PlayerReputation() {
            this.successfulTrades = 0;
            this.failedTrades = 0;
            this.disputes = 0;
            this.totalTradeValue = 0.0;
            this.reviews = new ArrayList<>();
        }
        
        public double getReputationScore() {
            if (successfulTrades + failedTrades == 0) return 50.0; // Neutral start
            double successRate = (double) successfulTrades / (successfulTrades + failedTrades);
            double disputePenalty = disputes * 5.0;
            return Math.max(0, Math.min(100, (successRate * 100) - disputePenalty));
        }
        
        public void addSuccessfulTrade(double value) {
            successfulTrades++;
            totalTradeValue += value;
        }
        
        public void addFailedTrade() {
            failedTrades++;
        }
        
        public void addDispute() {
            disputes++;
        }
        
        public int getSuccessfulTrades() { return successfulTrades; }
        public int getDisputes() { return disputes; }
        public double getTotalTradeValue() { return totalTradeValue; }
    }
    
    public static class TradeHistory {
        private final UUID traderId;
        private final String traderName;
        private final double amount;
        private final long timestamp;
        private final TradeStatus outcome;
        
        public TradeHistory(UUID traderId, String traderName, double amount, TradeStatus outcome) {
            this.traderId = traderId;
            this.traderName = traderName;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.outcome = outcome;
        }
    }
    
    /**
     * Initiate a secure trade with escrow protection
     */
    public boolean initiateSecureTrade(ServerPlayerEntity seller, ServerPlayerEntity buyer, ItemStack item, double price) {
        if (price <= 0) {
            seller.sendMessage(Text.literal("§c[SECURE TRADE] §fPrice must be positive!"), false);
            return false;
        }
        
        if (item.isEmpty()) {
            seller.sendMessage(Text.literal("§c[SECURE TRADE] §fYou must hold an item to trade!"), false);
            return false;
        }
        
        // Check seller reputation
        PlayerReputation sellerRep = getReputation(seller.getUuid());
        if (sellerRep.getReputationScore() < 20.0) {
            seller.sendMessage(Text.literal("§c[SECURE TRADE] §fYour reputation is too low to trade! Score: " + 
                String.format("%.1f", sellerRep.getReputationScore())), false);
            return false;
        }
        
        var economyManager = CountryMod.getEconomyManager();
        var buyerAccount = economyManager.getAccount(buyer.getUuid());
        
        if (buyerAccount == null || buyerAccount.getBalance() < price) {
            seller.sendMessage(Text.literal("§c[SECURE TRADE] §fBuyer has insufficient funds!"), false);
            buyer.sendMessage(Text.literal("§c[SECURE TRADE] §fYou need REI$ " + 
                String.format("%.2f", price) + " to accept this trade!"), false);
            return false;
        }
        
        SecureTrade trade = new SecureTrade(seller.getUuid(), buyer.getUuid(), item, price);
        activeTrades.put(trade.getTradeId(), trade);
        
        seller.sendMessage(Text.literal("§a[SECURE TRADE] §fTrade initiated with " + buyer.getName().getString() + 
            "\n§fItem: " + item.getName().getString() + " x" + item.getCount() + 
            "\n§fPrice: REI$ " + String.format("%.2f", price) + 
            "\n§fTrade ID: " + trade.getTradeId().toString().substring(0, 8) + 
            "\n§eWaiting for both parties to confirm..."), false);
        
        buyer.sendMessage(Text.literal("§6[SECURE TRADE] §fTrade request from " + seller.getName().getString() + 
            "\n§fItem: " + item.getName().getString() + " x" + item.getCount() + 
            "\n§fPrice: REI$ " + String.format("%.2f", price) + 
            "\n§fSeller Reputation: " + String.format("%.1f", sellerRep.getReputationScore()) + "/100" +
            "\n§fTrade ID: " + trade.getTradeId().toString().substring(0, 8) + 
            "\n§aUse /trade confirm <ID> to accept"), false);
        
        return true;
    }
    
    /**
     * Confirm trade (both parties must confirm)
     */
    public boolean confirmTrade(ServerPlayerEntity player, UUID tradeId) {
        SecureTrade trade = activeTrades.get(tradeId);
        
        if (trade == null) {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fTrade not found!"), false);
            return false;
        }
        
        if (trade.getStatus() != TradeStatus.PENDING) {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fTrade is no longer pending!"), false);
            return false;
        }
        
        if (player.getUuid().equals(trade.getSeller())) {
            trade.setSellerConfirmed(true);
            player.sendMessage(Text.literal("§a[SECURE TRADE] §fYou confirmed the trade!"), false);
        } else if (player.getUuid().equals(trade.getBuyer())) {
            trade.setBuyerConfirmed(true);
            player.sendMessage(Text.literal("§a[SECURE TRADE] §fYou confirmed the trade!"), false);
        } else {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fYou are not part of this trade!"), false);
            return false;
        }
        
        // If both confirmed, lock funds in escrow
        if (trade.isSellerConfirmed() && trade.isBuyerConfirmed()) {
            return lockEscrow(trade);
        }
        
        return true;
    }
    
    /**
     * Lock funds in escrow protection
     */
    private boolean lockEscrow(SecureTrade trade) {
        var economyManager = CountryMod.getEconomyManager();
        var buyerAccount = economyManager.getAccount(trade.getBuyer());
        
        if (buyerAccount == null || !buyerAccount.withdraw(trade.getPrice())) {
            trade.setStatus(TradeStatus.CANCELLED);
            notifyTradePlayers(trade, "§c[SECURE TRADE] §fTrade cancelled - buyer insufficient funds!");
            return false;
        }
        
        trade.setEscrowAmount(trade.getPrice());
        trade.setStatus(TradeStatus.ESCROW_LOCKED);
        
        notifyTradePlayers(trade, "§a[SECURE TRADE] §fFunds locked in escrow! " +
            "\n§eREI$ " + String.format("%.2f", trade.getPrice()) + " is being held securely." +
            "\n§fBuyer must confirm receipt of item with /trade complete <ID>");
        
        return true;
    }
    
    /**
     * Complete trade after buyer confirms receiving item
     */
    public boolean completeTrade(ServerPlayerEntity buyer, UUID tradeId) {
        SecureTrade trade = activeTrades.get(tradeId);
        
        if (trade == null || !trade.getBuyer().equals(buyer.getUuid())) {
            buyer.sendMessage(Text.literal("§c[SECURE TRADE] §fTrade not found or you're not the buyer!"), false);
            return false;
        }
        
        if (trade.getStatus() != TradeStatus.ESCROW_LOCKED) {
            buyer.sendMessage(Text.literal("§c[SECURE TRADE] §fTrade is not in escrow status!"), false);
            return false;
        }
        
        // Release escrow to seller
        var economyManager = CountryMod.getEconomyManager();
        var sellerAccount = economyManager.getAccount(trade.getSeller());
        
        if (sellerAccount != null) {
            sellerAccount.deposit(trade.getEscrowAmount(), "Secure Trade");
            trade.setStatus(TradeStatus.COMPLETED);
            
            // Update reputations
            getReputation(trade.getSeller()).addSuccessfulTrade(trade.getPrice());
            getReputation(trade.getBuyer()).addSuccessfulTrade(trade.getPrice());
            
            // Record history
            recordTradeHistory(trade);
            
            notifyTradePlayers(trade, "§a§l[SECURE TRADE] §aTrade completed successfully!" +
                "\n§fREI$ " + String.format("%.2f", trade.getPrice()) + " transferred to seller.");
            
            activeTrades.remove(tradeId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Dispute a trade
     */
    public boolean disputeTrade(ServerPlayerEntity player, UUID tradeId, String reason) {
        SecureTrade trade = activeTrades.get(tradeId);
        
        if (trade == null) {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fTrade not found!"), false);
            return false;
        }
        
        if (!trade.getSeller().equals(player.getUuid()) && !trade.getBuyer().equals(player.getUuid())) {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fYou are not part of this trade!"), false);
            return false;
        }
        
        trade.setStatus(TradeStatus.DISPUTED);
        getReputation(trade.getSeller()).addDispute();
        getReputation(trade.getBuyer()).addDispute();
        
        notifyTradePlayers(trade, "§c§l[SECURE TRADE] §cTRADE DISPUTED!" +
            "\n§fReason: " + reason +
            "\n§fEscrow funds frozen. Contact a server admin for resolution.");
        
        CountryMod.LOGGER.warn("Trade {} disputed by {}. Reason: {}", tradeId, player.getName().getString(), reason);
        return true;
    }
    
    /**
     * Cancel trade (before escrow locked)
     */
    public boolean cancelTrade(ServerPlayerEntity player, UUID tradeId) {
        SecureTrade trade = activeTrades.get(tradeId);
        
        if (trade == null) {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fTrade not found!"), false);
            return false;
        }
        
        if (trade.getStatus() == TradeStatus.ESCROW_LOCKED) {
            player.sendMessage(Text.literal("§c[SECURE TRADE] §fCannot cancel - funds in escrow! Use /trade dispute instead."), false);
            return false;
        }
        
        trade.setStatus(TradeStatus.CANCELLED);
        notifyTradePlayers(trade, "§e[SECURE TRADE] §fTrade cancelled.");
        activeTrades.remove(tradeId);
        
        return true;
    }
    
    private void notifyTradePlayers(SecureTrade trade, String message) {
        var server = CountryMod.getServer();
        if (server == null) return;
        
        ServerPlayerEntity seller = server.getPlayerManager().getPlayer(trade.getSeller());
        ServerPlayerEntity buyer = server.getPlayerManager().getPlayer(trade.getBuyer());
        
        if (seller != null) seller.sendMessage(Text.literal(message), false);
        if (buyer != null) buyer.sendMessage(Text.literal(message), false);
    }
    
    private void recordTradeHistory(SecureTrade trade) {
        var server = CountryMod.getServer();
        if (server == null) return;
        
        ServerPlayerEntity seller = server.getPlayerManager().getPlayer(trade.getSeller());
        ServerPlayerEntity buyer = server.getPlayerManager().getPlayer(trade.getBuyer());
        
        if (seller != null && buyer != null) {
            tradeHistories.computeIfAbsent(trade.getSeller(), k -> new ArrayList<>())
                .add(new TradeHistory(trade.getBuyer(), buyer.getName().getString(), trade.getPrice(), trade.getStatus()));
            
            tradeHistories.computeIfAbsent(trade.getBuyer(), k -> new ArrayList<>())
                .add(new TradeHistory(trade.getSeller(), seller.getName().getString(), trade.getPrice(), trade.getStatus()));
        }
    }
    
    public PlayerReputation getReputation(UUID playerId) {
        return reputations.computeIfAbsent(playerId, k -> new PlayerReputation());
    }
    
    public List<TradeHistory> getTradeHistory(UUID playerId) {
        return tradeHistories.getOrDefault(playerId, new ArrayList<>());
    }
    
    public Map<UUID, SecureTrade> getActiveTrades() {
        return new HashMap<>(activeTrades);
    }
}
