package com.countrymod.model;

/**
 * Defines the levels of citizenship within a country.
 * Each level has different permissions and access rights.
 */
public enum CitizenshipLevel {
	/**
	 * Visitor: Restricted entry, cannot build or access resources
	 * Requires visa approval to enter territory
	 */
	VISITOR("Visitor", 0),
	
	/**
	 * Resident: Limited rights, may need visa approval for certain actions
	 * Can stay in country but has restricted building permissions
	 */
	RESIDENT("Resident", 1),
	
	/**
	 * Citizen: Full rights, can build, vote, and join military
	 * Has all privileges within the country
	 */
	CITIZEN("Citizen", 2);
	
	private final String displayName;
	private final int level;
	
	CitizenshipLevel(String displayName, int level) {
		this.displayName = displayName;
		this.level = level;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getLevel() {
		return level;
	}
	
	/**
	 * Check if this citizenship level can build in the country
	 */
	public boolean canBuild() {
		return this == CITIZEN || this == RESIDENT;
	}
	
	/**
	 * Check if this citizenship level can vote
	 */
	public boolean canVote() {
		return this == CITIZEN;
	}
	
	/**
	 * Check if this citizenship level can join military
	 */
	public boolean canJoinMilitary() {
		return this == CITIZEN;
	}
}
