package com.countrymod.model;

/**
 * Defines military ranks within a country's armed forces.
 * Higher ranks have more authority and combat bonuses.
 */
public enum MilitaryRank {
	/**
	 * Soldier: Basic military member
	 * Standard combat capabilities
	 */
	SOLDIER("Soldier", 1, 1.0f),
	
	/**
	 * Captain: Mid-level officer
	 * Enhanced combat capabilities and can lead small units
	 */
	CAPTAIN("Captain", 2, 1.15f),
	
	/**
	 * General: High-ranking officer
	 * Superior combat capabilities and can command military operations
	 */
	GENERAL("General", 3, 1.3f);
	
	private final String displayName;
	private final int rankLevel;
	private final float combatBonus; // Multiplier for combat effectiveness
	
	MilitaryRank(String displayName, int rankLevel, float combatBonus) {
		this.displayName = displayName;
		this.rankLevel = rankLevel;
		this.combatBonus = combatBonus;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getRankLevel() {
		return rankLevel;
	}
	
	public float getCombatBonus() {
		return combatBonus;
	}
	
	/**
	 * Check if this rank can command others
	 */
	public boolean canCommand() {
		return this == CAPTAIN || this == GENERAL;
	}
	
	/**
	 * Check if this rank can declare war (requires authorization)
	 */
	public boolean canDeclareWar() {
		return this == GENERAL;
	}
}
