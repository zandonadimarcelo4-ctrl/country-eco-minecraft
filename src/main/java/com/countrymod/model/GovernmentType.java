package com.countrymod.model;

/**
 * Defines the types of governments that countries can have.
 * Each government type has different rules for leadership succession and takeover.
 */
public enum GovernmentType {
	/**
	 * Republic: Leader can be replaced via citizen voting
	 * Allows democratic leadership changes without PvP
	 */
	REPUBLIC("Republic"),
	
	/**
	 * Monarchy: Heir system with PvP takeover possible
	 * Leadership passes through designated heirs or can be taken by force
	 */
	MONARCHY("Monarchy"),
	
	/**
	 * Dictatorship: Only force (PvP) can replace leader
	 * No peaceful transition of power
	 */
	DICTATORSHIP("Dictatorship"),
	
	/**
	 * Tribe: Strongest player in combat becomes leader
	 * Leadership constantly challenged through strength
	 */
	TRIBE("Tribe");
	
	private final String displayName;
	
	GovernmentType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Check if this government type allows peaceful succession
	 */
	public boolean allowsPeacefulSuccession() {
		return this == REPUBLIC || this == MONARCHY;
	}
	
	/**
	 * Check if this government type allows PvP takeover
	 */
	public boolean allowsPvPTakeover() {
		return this == MONARCHY || this == DICTATORSHIP || this == TRIBE;
	}
}
