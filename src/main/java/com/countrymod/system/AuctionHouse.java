package com.countrymod.system;

import com.countrymod.CountryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Auction house system for competitive bidding on items
 */
public class AuctionHouse {
    private final Map<UUID, Auction> activeAuctions = new ConcurrentHashMap<>();
    private final Map<UUID, List<Auction>> playerAuctions = new ConcurrentHashMap<>();
    
    public static class Auction {
        private final UUID auctionId;
        private final UUID sellerId;
        private final String sellerName;
        private final ItemStack item;
        private final double startingBid;
        private final long endTime;
        private final long createdAt;
        
        private UUID highestBidderId;
        private String highestBidderName;
        private double highestBid;
        private final List<Bid> bidHistory;
        private AuctionStatus status;
        
        public Auction(UUID sellerId, String sellerName, ItemStack item, double startingBid, int durationMinutes) {
            this.auctionId = UUID.randomUUID();
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.item = item.copy();
            this.startingBid = startingBid;
            this.highestBid = startingBid;
            this.createdAt = System.currentTimeMillis();
            this.endTime = createdAt + (durationMinutes * 60 * 1000L);
            this.bidHistory = new ArrayList<>();
            this.status = AuctionStatus.ACTIVE;
        }
        
        public UUID getAuctionId() { return auctionId; }
        public UUID getSellerId() { return sellerId; }
        public String getSellerName() { return sellerName; }
        public ItemStack getItem() { return item; }
        public double getStartingBid() { return startingBid; }
        public double getHighestBid() { return highestBid; }
        public UUID getHighestBidderId() { return highestBidderId; }
        public String getHighestBidderName() { return highestBidderName; }
        public long getEndTime() { return endTime; }
        public AuctionStatus getStatus() { return status; }
        public List<Bid> getBidHistory() { return new ArrayList<>(bidHistory); }
        
        public boolean isExpired() {
            return System.currentTimeMillis() >= endTime;
        }
        
        public long getTimeRemaining() {
            return Math.max(0, endTime - System.currentTimeMillis());
        }
        
        public boolean placeBid(UUID bidderId, String bidderName, double amount) {
            if (isExpired() || status != AuctionStatus.ACTIVE) {
                return false;
            }
            
            if (bidderId.equals(sellerId)) {
                return false; // Seller can't bid on their own auction
            }
            
            double minBid = highestBid * 1.05; // Minimum 5% increase
            if (amount < minBid) {
                return false;
            }
            
            highestBidderId = bidderId;
            highestBidderName = bidderName;
            highestBid = amount;
            bidHistory.add(new Bid(bidderId, bidderName, amount, System.currentTimeMillis()));
            
            return true;
        }
        
        public void setStatus(AuctionStatus status) {
            this.status = status;
        }
    }
    
    public static class Bid {
        private final UUID bidderId;
        private final String bidderName;
        private final double amount;
        private final long timestamp;
        
        public Bid(UUID bidderId, String bidderName, double amount, long timestamp) {
            this.bidderId = bidderId;
            this.bidderName = bidderName;
            this.amount = amount;
            this.timestamp = timestamp;
        }
        
        public UUID getBidderId() { return bidderId; }
        public String getBidderName() { return bidderName; }
        public double getAmount() { return amount; }
        public long getTimestamp() { return timestamp; }
    }
    
    public enum AuctionStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED,
        EXPIRED_NO_BIDS
    }
    
    /**
     * Create a new auction
     */
    public boolean createAuction(ServerPlayerEntity seller, ItemStack item, double startingBid, int durationMinutes) {
        if (item.isEmpty()) {
            seller.sendMessage(Text.literal("§c[AUCTION] §fYou must hold an item to auction!"), false);
            return false;
        }
        
        if (startingBid <= 0) {
            seller.sendMessage(Text.literal("§c[AUCTION] §fStarting bid must be positive!"), false);
            return false;
        }
        
        if (durationMinutes < 5 || durationMinutes > 1440) { // 5 min to 24 hours
            seller.sendMessage(Text.literal("§c[AUCTION] §fDuration must be between 5 and 1440 minutes!"), false);
            return false;
        }
        
        Auction auction = new Auction(seller.getUuid(), seller.getName().getString(), item, startingBid, durationMinutes);
        activeAuctions.put(auction.getAuctionId(), auction);
        playerAuctions.computeIfAbsent(seller.getUuid(), k -> new ArrayList<>()).add(auction);
        
        seller.sendMessage(Text.literal("§a§l[AUCTION] §aAuction created!" +
            "\n§fItem: " + item.getName().getString() + " x" + item.getCount() +
            "\n§fStarting Bid: REI$ " + String.format("%.2f", startingBid) +
            "\n§fDuration: " + durationMinutes + " minutes" +
            "\n§fAuction ID: " + auction.getAuctionId().toString().substring(0, 8)), false);
        
        // Broadcast to all players
        broadcastNewAuction(auction);
        
        return true;
    }
    
    /**
     * Place a bid on an auction
     */
    public boolean placeBid(ServerPlayerEntity bidder, UUID auctionId, double bidAmount) {
        Auction auction = activeAuctions.get(auctionId);
        
        if (auction == null) {
            bidder.sendMessage(Text.literal("§c[AUCTION] §fAuction not found!"), false);
            return false;
        }
        
        if (auction.isExpired()) {
            bidder.sendMessage(Text.literal("§c[AUCTION] §fThis auction has ended!"), false);
            return false;
        }
        
        if (auction.getSellerId().equals(bidder.getUuid())) {
            bidder.sendMessage(Text.literal("§c[AUCTION] §fYou cannot bid on your own auction!"), false);
            return false;
        }
        
        double minBid = auction.getHighestBid() * 1.05; // 5% minimum increase
        if (bidAmount < minBid) {
            bidder.sendMessage(Text.literal("§c[AUCTION] §fBid too low! Minimum: REI$ " + 
                String.format("%.2f", minBid)), false);
            return false;
        }
        
        var economyManager = CountryMod.getEconomyManager();
        var bidderAccount = economyManager.getAccount(bidder.getUuid());
        
        if (bidderAccount == null || bidderAccount.getBalance() < bidAmount) {
            bidder.sendMessage(Text.literal("§c[AUCTION] §fInsufficient funds!"), false);
            return false;
        }
        
        // Refund previous highest bidder
        if (auction.getHighestBidderId() != null) {
            var previousBidder = economyManager.getAccount(auction.getHighestBidderId());
            if (previousBidder != null) {
                previousBidder.deposit(auction.getHighestBid(), "Auction Bid Refund");
                
                // Notify previous bidder
                var server = CountryMod.getServer();
                if (server != null) {
                    ServerPlayerEntity prevPlayer = server.getPlayerManager().getPlayer(auction.getHighestBidderId());
                    if (prevPlayer != null) {
                        prevPlayer.sendMessage(Text.literal("§e[AUCTION] §fYou've been outbid on " + 
                            auction.getItem().getName().getString() + "!"), false);
                    }
                }
            }
        }
        
        // Lock new bid funds
        if (!bidderAccount.withdraw(bidAmount)) {
            return false;
        }
        
        if (auction.placeBid(bidder.getUuid(), bidder.getName().getString(), bidAmount)) {
            bidder.sendMessage(Text.literal("§a[AUCTION] §aBid placed successfully!" +
                "\n§fYour Bid: REI$ " + String.format("%.2f", bidAmount) +
                "\n§fTime Remaining: " + formatTimeRemaining(auction.getTimeRemaining())), false);
            
            // Notify seller
            notifySeller(auction, "New bid: REI$ " + String.format("%.2f", bidAmount) + 
                " by " + bidder.getName().getString());
            
            return true;
        }
        
        // Refund if bid failed
        bidderAccount.deposit(bidAmount, "Auction Bid Failed");
        return false;
    }
    
    /**
     * Complete expired auctions
     */
    public void processExpiredAuctions() {
        var server = CountryMod.getServer();
        if (server == null) return;
        
        var economyManager = CountryMod.getEconomyManager();
        
        for (Auction auction : new ArrayList<>(activeAuctions.values())) {
            if (auction.isExpired() && auction.getStatus() == AuctionStatus.ACTIVE) {
                if (auction.getHighestBidderId() != null) {
                    // Auction sold - transfer item and funds
                    auction.setStatus(AuctionStatus.COMPLETED);
                    
                    // Pay seller
                    var sellerAccount = economyManager.getAccount(auction.getSellerId());
                    if (sellerAccount != null) {
                        double fee = auction.getHighestBid() * 0.05; // 5% auction house fee
                        double sellerProceeds = auction.getHighestBid() - fee;
                        sellerAccount.deposit(sellerProceeds, "Auction Sale");
                    }
                    
                    // Give item to winner
                    ServerPlayerEntity winner = server.getPlayerManager().getPlayer(auction.getHighestBidderId());
                    if (winner != null) {
                        winner.getInventory().insertStack(auction.getItem().copy());
                        winner.sendMessage(Text.literal("§a§l[AUCTION] §aYou won the auction!" +
                            "\n§fItem: " + auction.getItem().getName().getString() + 
                            "\n§fFinal Price: REI$ " + String.format("%.2f", auction.getHighestBid())), false);
                    }
                    
                    // Notify seller
                    notifySeller(auction, "§a§lAuction completed! Sold for REI$ " + 
                        String.format("%.2f", auction.getHighestBid()));
                    
                } else {
                    // No bids - return item
                    auction.setStatus(AuctionStatus.EXPIRED_NO_BIDS);
                    notifySeller(auction, "§eAuction expired with no bids.");
                }
                
                activeAuctions.remove(auction.getAuctionId());
            }
        }
    }
    
    /**
     * Get all active auctions
     */
    public List<Auction> getActiveAuctions() {
        return new ArrayList<>(activeAuctions.values()).stream()
            .filter(a -> a.getStatus() == AuctionStatus.ACTIVE && !a.isExpired())
            .toList();
    }
    
    private void broadcastNewAuction(Auction auction) {
        var server = CountryMod.getServer();
        if (server == null) return;
        
        String message = "§6§l[AUCTION] §6New auction: " + auction.getItem().getName().getString() + 
            " §fStarting bid: REI$ " + String.format("%.2f", auction.getStartingBid());
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!player.getUuid().equals(auction.getSellerId())) {
                player.sendMessage(Text.literal(message), false);
            }
        }
    }
    
    private void notifySeller(Auction auction, String message) {
        var server = CountryMod.getServer();
        if (server == null) return;
        
        ServerPlayerEntity seller = server.getPlayerManager().getPlayer(auction.getSellerId());
        if (seller != null) {
            seller.sendMessage(Text.literal("§6[AUCTION] §f" + message), false);
        }
    }
    
    private String formatTimeRemaining(long milliseconds) {
        long minutes = milliseconds / (60 * 1000);
        if (minutes < 60) {
            return minutes + " minutes";
        }
        long hours = minutes / 60;
        return hours + " hours, " + (minutes % 60) + " minutes";
    }
}
