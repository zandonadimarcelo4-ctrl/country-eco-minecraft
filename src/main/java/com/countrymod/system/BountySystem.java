package com.countrymod.system;

import java.util.*;

/**
 * Bounty Hunter system - put bounties on players' heads!
 * Kill the target to claim the reward in REI$.
 */
public class BountySystem {
	private Map<UUID, Bounty> activeBounties; // targetUuid -> Bounty
	
	public BountySystem() {
		this.activeBounties = new HashMap<>();
	}
	
	public boolean placeBounty(UUID targetUuid, String targetName, UUID placedBy, String placedByName, double reward, String reason) {
		if (!activeBounties.containsKey(targetUuid)) {
			Bounty bounty = new Bounty(targetUuid, targetName, placedBy, placedByName, reward, reason);
			activeBounties.put(targetUuid, bounty);
			return true;
		} else {
			// Add to existing bounty
			Bounty existing = activeBounties.get(targetUuid);
			existing.reward += reward;
			return true;
		}
	}
	
	public Bounty claimBounty(UUID targetUuid, UUID hunterUuid, String hunterName) {
		Bounty bounty = activeBounties.remove(targetUuid);
		if (bounty != null) {
			bounty.claimed = true;
			bounty.claimedBy = hunterUuid;
			bounty.claimedByName = hunterName;
			bounty.claimedTime = System.currentTimeMillis();
		}
		return bounty;
	}
	
	public Bounty getBounty(UUID targetUuid) {
		return activeBounties.get(targetUuid);
	}
	
	public boolean hasBounty(UUID playerUuid) {
		return activeBounties.containsKey(playerUuid);
	}
	
	public Collection<Bounty> getAllBounties() {
		return activeBounties.values();
	}
	
	public List<Bounty> getTopBounties(int limit) {
		return activeBounties.values().stream()
			.sorted((b1, b2) -> Double.compare(b2.reward, b1.reward))
			.limit(limit)
			.toList();
	}
	
	public static class Bounty {
		private UUID targetUuid;
		private String targetName;
		private UUID placedBy;
		private String placedByName;
		private double reward;
		private String reason;
		private long placedTime;
		private boolean claimed;
		private UUID claimedBy;
		private String claimedByName;
		private long claimedTime;
		
		public Bounty(UUID targetUuid, String targetName, UUID placedBy, String placedByName, double reward, String reason) {
			this.targetUuid = targetUuid;
			this.targetName = targetName;
			this.placedBy = placedBy;
			this.placedByName = placedByName;
			this.reward = reward;
			this.reason = reason;
			this.placedTime = System.currentTimeMillis();
			this.claimed = false;
		}
		
		public UUID getTargetUuid() { return targetUuid; }
		public String getTargetName() { return targetName; }
		public double getReward() { return reward; }
		public String getReason() { return reason; }
		public boolean isClaimed() { return claimed; }
		public UUID getClaimedBy() { return claimedBy; }
		public String getClaimedByName() { return claimedByName; }
		
		public String getFormattedBounty() {
			return String.format("§c[WANTED] §f%s §7- §2REI$ %.2f §7- §e%s", targetName, reward, reason);
		}
	}
}
