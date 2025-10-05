package com.countrymod.system;

import java.util.*;

/**
 * Player Rank/Level System - Progress through ranks by earning XP!
 * Ranks give perks, titles, and unlock special features.
 */
public class RankSystem {
	private Map<UUID, PlayerRank> playerRanks;
	
	public RankSystem() {
		this.playerRanks = new HashMap<>();
	}
	
	public PlayerRank getRank(UUID playerUuid) {
		return playerRanks.computeIfAbsent(playerUuid, k -> new PlayerRank(playerUuid));
	}
	
	public void addXP(UUID playerUuid, int xp, String reason) {
		PlayerRank rank = getRank(playerUuid);
		rank.addXP(xp, reason);
	}
	
	public enum Rank {
		PEASANT("Peasant", 0, "§7", "Start your journey"),
		COMMONER("Commoner", 100, "§f", "Basic citizen"),
		MERCHANT("Merchant", 500, "§e", "Trade unlocked, 5% discount"),
		NOBLE("Noble", 1500, "§6", "Can own property, 10% tax reduction"),
		BARON("Baron", 3000, "§b", "Can hire workers, special items"),
		DUKE("Duke", 6000, "§9", "Diplomatic immunity, VIP access"),
		PRINCE("Prince", 10000, "§d", "Royal privileges, castle access"),
		KING("King", 20000, "§5§l", "Supreme authority, all perks"),
		EMPEROR("Emperor", 50000, "§c§l§n", "Legendary status, god mode");
		
		private final String displayName;
		private final int xpRequired;
		private final String color;
		private final String perk;
		
		Rank(String displayName, int xpRequired, String color, String perk) {
			this.displayName = displayName;
			this.xpRequired = xpRequired;
			this.color = color;
			this.perk = perk;
		}
		
		public String getDisplayName() { return displayName; }
		public int getXpRequired() { return xpRequired; }
		public String getColor() { return color; }
		public String getPerk() { return perk; }
		
		public String getColoredName() {
			return color + displayName;
		}
		
		public static Rank fromXP(int totalXP) {
			Rank[] ranks = values();
			for (int i = ranks.length - 1; i >= 0; i--) {
				if (totalXP >= ranks[i].xpRequired) {
					return ranks[i];
				}
			}
			return PEASANT;
		}
	}
	
	public static class PlayerRank {
		private UUID playerUuid;
		private int totalXP;
		private Rank currentRank;
		private List<XPGain> xpHistory;
		private Set<String> unlockedPerks;
		
		public PlayerRank(UUID playerUuid) {
			this.playerUuid = playerUuid;
			this.totalXP = 0;
			this.currentRank = Rank.PEASANT;
			this.xpHistory = new ArrayList<>();
			this.unlockedPerks = new HashSet<>();
		}
		
		public void addXP(int xp, String reason) {
			totalXP += xp;
			xpHistory.add(new XPGain(xp, reason, System.currentTimeMillis()));
			
			// Keep only last 100 XP gains
			if (xpHistory.size() > 100) {
				xpHistory.remove(0);
			}
			
			// Update rank
			Rank newRank = Rank.fromXP(totalXP);
			if (newRank != currentRank) {
				currentRank = newRank;
				// Unlock perk
				unlockedPerks.add(newRank.getPerk());
			}
		}
		
		public int getTotalXP() { return totalXP; }
		public Rank getCurrentRank() { return currentRank; }
		public Set<String> getUnlockedPerks() { return unlockedPerks; }
		
		public int getXPToNextRank() {
			Rank[] ranks = Rank.values();
			int currentIndex = currentRank.ordinal();
			if (currentIndex < ranks.length - 1) {
				return ranks[currentIndex + 1].xpRequired - totalXP;
			}
			return 0; // Max rank
		}
		
		public double getProgressToNextRank() {
			Rank[] ranks = Rank.values();
			int currentIndex = currentRank.ordinal();
			if (currentIndex < ranks.length - 1) {
				int currentReq = currentRank.xpRequired;
				int nextReq = ranks[currentIndex + 1].xpRequired;
				int progress = totalXP - currentReq;
				int total = nextReq - currentReq;
				return (double) progress / total * 100.0;
			}
			return 100.0; // Max rank
		}
	}
	
	public static class XPGain {
		private int amount;
		private String reason;
		private long timestamp;
		
		public XPGain(int amount, String reason, long timestamp) {
			this.amount = amount;
			this.reason = reason;
			this.timestamp = timestamp;
		}
		
		public int getAmount() { return amount; }
		public String getReason() { return reason; }
		public long getTimestamp() { return timestamp; }
	}
}
