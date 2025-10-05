package com.countrymod.system;

import java.util.*;

/**
 * Marriage system - players can marry, merge finances, and get tax benefits!
 * Married couples share resources and get bonuses in the country.
 */
public class MarriageSystem {
	private Map<UUID, Marriage> marriages; // playerUuid -> Marriage
	
	public MarriageSystem() {
		this.marriages = new HashMap<>();
	}
	
	public Marriage proposeMarriage(UUID proposer, String proposerName, UUID partner, String partnerName) {
		if (!isMarried(proposer) && !isMarried(partner)) {
			Marriage marriage = new Marriage(proposer, proposerName, partner, partnerName);
			return marriage;
		}
		return null;
	}
	
	public boolean acceptProposal(Marriage marriage) {
		if (marriage != null && !marriage.isAccepted()) {
			marriage.accepted = true;
			marriage.marriageDate = System.currentTimeMillis();
			marriages.put(marriage.player1Uuid, marriage);
			marriages.put(marriage.player2Uuid, marriage);
			return true;
		}
		return false;
	}
	
	public boolean divorce(UUID playerUuid, double divorceCost) {
		Marriage marriage = marriages.get(playerUuid);
		if (marriage != null) {
			// Remove from both players
			marriages.remove(marriage.player1Uuid);
			marriages.remove(marriage.player2Uuid);
			
			// Divorce costs money!
			return true;
		}
		return false;
	}
	
	public boolean isMarried(UUID playerUuid) {
		return marriages.containsKey(playerUuid);
	}
	
	public Marriage getMarriage(UUID playerUuid) {
		return marriages.get(playerUuid);
	}
	
	public UUID getSpouse(UUID playerUuid) {
		Marriage marriage = marriages.get(playerUuid);
		if (marriage != null) {
			return marriage.player1Uuid.equals(playerUuid) ? marriage.player2Uuid : marriage.player1Uuid;
		}
		return null;
	}
	
	public double getTaxBenefit() {
		return 0.15; // 15% tax reduction for married couples
	}
	
	public static class Marriage {
		private UUID player1Uuid;
		private String player1Name;
		private UUID player2Uuid;
		private String player2Name;
		private long proposalDate;
		private long marriageDate;
		private boolean accepted;
		
		public Marriage(UUID player1Uuid, String player1Name, UUID player2Uuid, String player2Name) {
			this.player1Uuid = player1Uuid;
			this.player1Name = player1Name;
			this.player2Uuid = player2Uuid;
			this.player2Name = player2Name;
			this.proposalDate = System.currentTimeMillis();
			this.accepted = false;
		}
		
		public boolean isAccepted() { return accepted; }
		public UUID getPlayer1Uuid() { return player1Uuid; }
		public UUID getPlayer2Uuid() { return player2Uuid; }
		public String getPlayer1Name() { return player1Name; }
		public String getPlayer2Name() { return player2Name; }
		public long getMarriageDate() { return marriageDate; }
		
		public long getDaysMarried() {
			if (!accepted) return 0;
			return (System.currentTimeMillis() - marriageDate) / (1000 * 60 * 60 * 24);
		}
	}
}
