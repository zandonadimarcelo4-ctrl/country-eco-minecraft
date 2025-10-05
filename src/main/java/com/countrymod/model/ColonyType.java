package com.countrymod.model;

/**
 * Defines the types of colonies that can be established.
 */
public enum ColonyType {
	/**
	 * Exploration colony: Temporary, for resource gathering
	 * Cannot request independence
	 */
	EXPLORATION("Exploration"),
	
	/**
	 * Settlement colony: Permanent, hosts citizens, generates resources
	 * Can request independence after meeting requirements
	 */
	SETTLEMENT("Settlement");
	
	private final String displayName;
	
	ColonyType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public boolean isPermanent() {
		return this == SETTLEMENT;
	}
}
