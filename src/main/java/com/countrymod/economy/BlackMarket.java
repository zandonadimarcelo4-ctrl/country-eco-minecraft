package com.countrymod.economy;

import java.util.*;

/**
 * Black Market system for underground economy and illegal trades.
 * Players can buy/sell contraband items anonymously for profit!
 */
public class BlackMarket {
	private static final Random random = new Random();
	
	public enum ContrabandItem {
		STOLEN_DIAMONDS("Stolen Diamonds", 1000.0, 3000.0),
		FAKE_CPF("Fake CPF Documents", 500.0, 1500.0),
		COUNTERFEIT_MONEY("Counterfeit REI$", 100.0, 500.0),
		MILITARY_SECRETS("Military Intelligence", 2000.0, 5000.0),
		FORBIDDEN_POTIONS("Forbidden Potions", 300.0, 800.0);
		
		private final String displayName;
		private final double minPrice;
		private final double maxPrice;
		
		ContrabandItem(String displayName, double minPrice, double maxPrice) {
			this.displayName = displayName;
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public double getCurrentPrice() {
			return minPrice + random.nextDouble() * (maxPrice - minPrice);
		}
	}
	
	private Map<UUID, List<BlackMarketTransaction>> playerTransactions;
	
	public BlackMarket() {
		this.playerTransactions = new HashMap<>();
	}
	
	public boolean buyContraband(UUID playerUuid, ContrabandItem item, PlayerAccount account) {
		double price = item.getCurrentPrice();
		
		if (account.removeBalance(price)) {
			BlackMarketTransaction transaction = new BlackMarketTransaction(
				playerUuid, item, price, true
			);
			playerTransactions.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(transaction);
			
			// 20% chance of getting caught!
			if (random.nextDouble() < 0.2) {
				account.setCreditScore(account.getCreditScore() - 100);
				return false; // Caught by authorities!
			}
			return true;
		}
		return false;
	}
	
	public double sellContraband(UUID playerUuid, ContrabandItem item, PlayerAccount account) {
		double price = item.getCurrentPrice() * 0.8; // Sell for 80% of buy price
		
		account.addBalance(price);
		BlackMarketTransaction transaction = new BlackMarketTransaction(
			playerUuid, item, price, false
		);
		playerTransactions.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(transaction);
		
		return price;
	}
	
	public List<BlackMarketTransaction> getPlayerHistory(UUID playerUuid) {
		return playerTransactions.getOrDefault(playerUuid, new ArrayList<>());
	}
	
	public static class BlackMarketTransaction {
		private UUID playerUuid;
		private ContrabandItem item;
		private double price;
		private boolean isBuy;
		private long timestamp;
		
		public BlackMarketTransaction(UUID playerUuid, ContrabandItem item, double price, boolean isBuy) {
			this.playerUuid = playerUuid;
			this.item = item;
			this.price = price;
			this.isBuy = isBuy;
			this.timestamp = System.currentTimeMillis();
		}
		
		public ContrabandItem getItem() { return item; }
		public double getPrice() { return price; }
		public boolean isBuy() { return isBuy; }
		public long getTimestamp() { return timestamp; }
	}
}
