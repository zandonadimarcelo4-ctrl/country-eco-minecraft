package com.countrymod.system;

import com.countrymod.CountryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Player-to-player trading system with escrow protection
 */
public class TradeSystem {
    private final Map<UUID, TradeOffer> activeOffers = new HashMap<>();
    private final Map<UUID, TradeOffer> pendingTrades = new HashMap<>();
    
    public static class TradeOffer {
        private final UUID sellerId;
        private final String sellerName;
        private final ItemStack item;
        private final double price;
        private final long createdAt;
        
        public TradeOffer(UUID sellerId, String sellerName, ItemStack item, double price) {
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.item = item;
            this.price = price;
            this.createdAt = System.currentTimeMillis();
        }
        
        public UUID getSellerId() { return sellerId; }
        public String getSellerName() { return sellerName; }
        public ItemStack getItem() { return item; }
        public double getPrice() { return price; }
        public long getCreatedAt() { return createdAt; }
    }
    
    public boolean createOffer(ServerPlayerEntity seller, ItemStack item, double price) {
        if (price <= 0) {
            seller.sendMessage(Text.literal("§c[TRADE] §fPrice must be positive!"), false);
            return false;
        }
        
        if (item.isEmpty()) {
            seller.sendMessage(Text.literal("§c[TRADE] §fYou must hold an item to sell!"), false);
            return false;
        }
        
        UUID offerId = UUID.randomUUID();
        TradeOffer offer = new TradeOffer(seller.getUuid(), seller.getName().getString(), item.copy(), price);
        activeOffers.put(offerId, offer);
        
        seller.sendMessage(Text.literal("§a[TRADE] §fOffer created! Item: " + 
            item.getName().getString() + " x" + item.getCount() + " for REI$ " + 
            String.format("%.2f", price)), false);
        
        return true;
    }
    
    public List<TradeOffer> getAllOffers() {
        return new ArrayList<>(activeOffers.values());
    }
    
    public boolean purchaseItem(ServerPlayerEntity buyer, UUID offerId) {
        TradeOffer offer = activeOffers.get(offerId);
        
        if (offer == null) {
            buyer.sendMessage(Text.literal("§c[TRADE] §fOffer not found!"), false);
            return false;
        }
        
        if (offer.getSellerId().equals(buyer.getUuid())) {
            buyer.sendMessage(Text.literal("§c[TRADE] §fYou cannot buy your own item!"), false);
            return false;
        }
        
        var economyManager = CountryMod.getEconomyManager();
        var buyerAccount = economyManager.getAccount(buyer.getUuid());
        var sellerAccount = economyManager.getAccount(offer.getSellerId());
        
        if (buyerAccount == null || sellerAccount == null) {
            buyer.sendMessage(Text.literal("§c[TRADE] §fAccount error!"), false);
            return false;
        }
        
        if (buyerAccount.getBalance() < offer.getPrice()) {
            buyer.sendMessage(Text.literal("§c[TRADE] §fInsufficient funds! Need REI$ " + 
                String.format("%.2f", offer.getPrice())), false);
            return false;
        }
        
        // Complete transaction
        if (!buyerAccount.withdraw(offer.getPrice())) {
            return false;
        }
        sellerAccount.deposit(offer.getPrice(), "Trade Sale");
        
        // Give item to buyer
        buyer.getInventory().insertStack(offer.getItem().copy());
        
        buyer.sendMessage(Text.literal("§a[TRADE] §fPurchased " + offer.getItem().getName().getString() + 
            " x" + offer.getItem().getCount() + " for REI$ " + String.format("%.2f", offer.getPrice())), false);
        
        // Notify seller if online
        var server = CountryMod.getServer();
        if (server != null) {
            ServerPlayerEntity seller = server.getPlayerManager().getPlayer(offer.getSellerId());
            if (seller != null) {
                seller.sendMessage(Text.literal("§a[TRADE] §f" + buyer.getName().getString() + 
                    " purchased your " + offer.getItem().getName().getString() + 
                    " for REI$ " + String.format("%.2f", offer.getPrice())), false);
            }
        }
        
        activeOffers.remove(offerId);
        return true;
    }
    
    public void cancelOffer(UUID sellerId, UUID offerId) {
        TradeOffer offer = activeOffers.get(offerId);
        
        if (offer != null && offer.getSellerId().equals(sellerId)) {
            activeOffers.remove(offerId);
        }
    }
    
    public void cleanupExpiredOffers() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = 24 * 60 * 60 * 1000; // 24 hours
        
        activeOffers.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue().getCreatedAt()) > expirationTime);
    }
}
