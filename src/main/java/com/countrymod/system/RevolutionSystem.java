package com.countrymod.system;

import com.countrymod.model.Country;
import java.util.*;

/**
 * Revolution system - citizens can organize protests and overthrow corrupt leaders!
 * When morale is low or corruption is high, revolutions can happen!
 */
public class RevolutionSystem {
	private Map<UUID, Revolution> activeRevolutions; // countryId -> Revolution
	
	public RevolutionSystem() {
		this.activeRevolutions = new HashMap<>();
	}
	
	public Revolution startRevolution(UUID countryId, UUID leaderUuid, String reason) {
		if (!activeRevolutions.containsKey(countryId)) {
			Revolution revolution = new Revolution(countryId, leaderUuid, reason);
			activeRevolutions.put(countryId, revolution);
			return revolution;
		}
		return null;
	}
	
	public boolean joinRevolution(UUID countryId, UUID playerUuid, String playerName) {
		Revolution revolution = activeRevolutions.get(countryId);
		if (revolution != null && !revolution.isCompleted()) {
			revolution.addSupporter(playerUuid, playerName);
			return true;
		}
		return false;
	}
	
	public boolean supportLeader(UUID countryId, UUID playerUuid, String playerName) {
		Revolution revolution = activeRevolutions.get(countryId);
		if (revolution != null && !revolution.isCompleted()) {
			revolution.addLoyalist(playerUuid, playerName);
			return true;
		}
		return false;
	}
	
	public RevolutionResult resolveRevolution(UUID countryId) {
		Revolution revolution = activeRevolutions.remove(countryId);
		if (revolution != null) {
			return revolution.resolve();
		}
		return null;
	}
	
	public Revolution getActiveRevolution(UUID countryId) {
		return activeRevolutions.get(countryId);
	}
	
	public static class Revolution {
		private UUID countryId;
		private UUID targetLeaderUuid;
		private String reason;
		private long startTime;
		private Set<UUID> supporters;
		private Set<UUID> loyalists;
		private boolean completed;
		private RevolutionResult result;
		
		public Revolution(UUID countryId, UUID targetLeaderUuid, String reason) {
			this.countryId = countryId;
			this.targetLeaderUuid = targetLeaderUuid;
			this.reason = reason;
			this.startTime = System.currentTimeMillis();
			this.supporters = new HashSet<>();
			this.loyalists = new HashSet<>();
			this.completed = false;
		}
		
		public void addSupporter(UUID playerUuid, String playerName) {
			supporters.add(playerUuid);
			loyalists.remove(playerUuid); // Can't be both
		}
		
		public void addLoyalist(UUID playerUuid, String playerName) {
			loyalists.add(playerUuid);
			supporters.remove(playerUuid); // Can't be both
		}
		
		public RevolutionResult resolve() {
			completed = true;
			
			int supportCount = supporters.size();
			int loyalCount = loyalists.size();
			
			boolean success = supportCount > loyalCount;
			
			result = new RevolutionResult(
				countryId,
				success,
				supportCount,
				loyalCount,
				reason
			);
			
			return result;
		}
		
		public boolean isCompleted() { return completed; }
		public int getSupporterCount() { return supporters.size(); }
		public int getLoyalistCount() { return loyalists.size(); }
		public String getReason() { return reason; }
	}
	
	public static class RevolutionResult {
		private UUID countryId;
		private boolean success;
		private int supporterCount;
		private int loyalistCount;
		private String reason;
		
		public RevolutionResult(UUID countryId, boolean success, int supporterCount, int loyalistCount, String reason) {
			this.countryId = countryId;
			this.success = success;
			this.supporterCount = supporterCount;
			this.loyalistCount = loyalistCount;
			this.reason = reason;
		}
		
		public boolean isSuccess() { return success; }
		public UUID getCountryId() { return countryId; }
		public int getSupporterCount() { return supporterCount; }
		public int getLoyalistCount() { return loyalistCount; }
		
		public String getResultMessage() {
			if (success) {
				return String.format("§c§lREVOLUTION SUCCESSFUL! §f%d supporters overthrew the government! Reason: %s", supporterCount, reason);
			} else {
				return String.format("§a§lREVOLUTION FAILED! §f%d loyalists defended the government against %d rebels.", loyalistCount, supporterCount);
			}
		}
	}
}
