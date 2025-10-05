package com.countrymod.model;

import net.minecraft.util.math.BlockPos;
import java.util.*;

/**
 * Represents a country with government, citizens, territory, and military.
 * Core data structure for the country system.
 */
public class Country {
	private UUID countryId;
	private String name;
	private GovernmentType governmentType;
	private BlockPos flagPosition; // Location of the country flag
	private UUID leaderId; // UUID of the current leader
	private Map<UUID, Citizen> citizens; // All members (visitors, residents, citizens)
	private List<Colony> colonies;
	private Territory territory;
	private Treasury treasury;
	private long foundedDate;
	
	// Takeover mechanics
	private boolean underAttack;
	private UUID attackerId; // Player attempting takeover
	private long attackStartTime;
	private static final long TAKEOVER_WINDOW = 300000; // 5 minutes in milliseconds
	
	// Cooldowns
	private long lastTakeoverAttempt;
	private static final long TAKEOVER_COOLDOWN = 3600000; // 1 hour in milliseconds
	
	public Country(String name, GovernmentType governmentType, BlockPos flagPosition, UUID leaderId) {
		this.countryId = UUID.randomUUID();
		this.name = name;
		this.governmentType = governmentType;
		this.flagPosition = flagPosition;
		this.leaderId = leaderId;
		this.citizens = new HashMap<>();
		this.colonies = new ArrayList<>();
		this.territory = new Territory();
		this.treasury = new Treasury();
		this.foundedDate = System.currentTimeMillis();
		this.underAttack = false;
		this.attackerId = null;
		this.attackStartTime = 0;
		this.lastTakeoverAttempt = 0;
	}
	
	// Getters and setters
	public UUID getCountryId() {
		return countryId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public GovernmentType getGovernmentType() {
		return governmentType;
	}
	
	public void setGovernmentType(GovernmentType governmentType) {
		this.governmentType = governmentType;
	}
	
	public BlockPos getFlagPosition() {
		return flagPosition;
	}
	
	public UUID getLeaderId() {
		return leaderId;
	}
	
	public void setLeaderId(UUID leaderId) {
		this.leaderId = leaderId;
	}
	
	public Citizen getLeader() {
		return citizens.get(leaderId);
	}
	
	public Map<UUID, Citizen> getCitizens() {
		return citizens;
	}
	
	public void addCitizen(Citizen citizen) {
		citizens.put(citizen.getPlayerUuid(), citizen);
	}
	
	public void removeCitizen(UUID playerUuid) {
		citizens.remove(playerUuid);
	}
	
	public Citizen getCitizen(UUID playerUuid) {
		return citizens.get(playerUuid);
	}
	
	public List<Colony> getColonies() {
		return colonies;
	}
	
	public void addColony(Colony colony) {
		colonies.add(colony);
	}
	
	public void removeColony(Colony colony) {
		colonies.remove(colony);
	}
	
	public Territory getTerritory() {
		return territory;
	}
	
	public Treasury getTreasury() {
		return treasury;
	}
	
	public long getFoundedDate() {
		return foundedDate;
	}
	
	// Takeover mechanics
	public boolean isUnderAttack() {
		return underAttack;
	}
	
	public void startTakeover(UUID attackerId) {
		this.underAttack = true;
		this.attackerId = attackerId;
		this.attackStartTime = System.currentTimeMillis();
		this.lastTakeoverAttempt = System.currentTimeMillis();
	}
	
	public void cancelTakeover() {
		this.underAttack = false;
		this.attackerId = null;
		this.attackStartTime = 0;
	}
	
	public boolean isTakeoverWindowActive() {
		if (!underAttack) return false;
		long elapsed = System.currentTimeMillis() - attackStartTime;
		return elapsed < TAKEOVER_WINDOW;
	}
	
	public UUID getAttackerId() {
		return attackerId;
	}
	
	public boolean canBeAttacked() {
		long timeSinceLastAttack = System.currentTimeMillis() - lastTakeoverAttempt;
		return timeSinceLastAttack >= TAKEOVER_COOLDOWN;
	}
	
	/**
	 * Complete a takeover - change leadership to the attacker
	 */
	public void completeTakeover(UUID newLeaderId) {
		// Remove leader status from old leader
		Citizen oldLeader = getLeader();
		if (oldLeader != null) {
			oldLeader.setLeader(false);
		}
		
		// Set new leader
		this.leaderId = newLeaderId;
		Citizen newLeader = getCitizen(newLeaderId);
		if (newLeader != null) {
			newLeader.setLeader(true);
			newLeader.setCitizenshipLevel(CitizenshipLevel.CITIZEN);
		}
		
		cancelTakeover();
	}
	
	/**
	 * Get all citizens of a specific citizenship level
	 */
	public List<Citizen> getCitizensByLevel(CitizenshipLevel level) {
		List<Citizen> result = new ArrayList<>();
		for (Citizen citizen : citizens.values()) {
			if (citizen.getCitizenshipLevel() == level) {
				result.add(citizen);
			}
		}
		return result;
	}
	
	/**
	 * Get all military members
	 */
	public List<Citizen> getMilitaryMembers() {
		List<Citizen> result = new ArrayList<>();
		for (Citizen citizen : citizens.values()) {
			if (citizen.isInMilitary()) {
				result.add(citizen);
			}
		}
		return result;
	}
}
