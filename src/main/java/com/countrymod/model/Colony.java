package com.countrymod.model;

import net.minecraft.util.math.BlockPos;
import java.util.UUID;

/**
 * Represents a colony established by a parent country.
 * Colonies can request independence and become new countries.
 */
public class Colony {
	private UUID colonyId;
	private String name;
	private UUID parentCountryId;
	private BlockPos flagPosition;
	private UUID governorId; // Player appointed as governor
	private ColonyType type;
	private Territory territory;
	private long establishedDate;
	
	// Independence mechanics
	private boolean independenceRequested;
	private long independenceRequestDate;
	private static final long INDEPENDENCE_REQUIREMENT_TIME = 604800000; // 7 days in milliseconds
	
	public Colony(String name, UUID parentCountryId, BlockPos flagPosition, UUID governorId, ColonyType type) {
		this.colonyId = UUID.randomUUID();
		this.name = name;
		this.parentCountryId = parentCountryId;
		this.flagPosition = flagPosition;
		this.governorId = governorId;
		this.type = type;
		this.territory = new Territory();
		this.establishedDate = System.currentTimeMillis();
		this.independenceRequested = false;
		this.independenceRequestDate = 0;
	}
	
	// Getters and setters
	public UUID getColonyId() {
		return colonyId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public UUID getParentCountryId() {
		return parentCountryId;
	}
	
	public BlockPos getFlagPosition() {
		return flagPosition;
	}
	
	public UUID getGovernorId() {
		return governorId;
	}
	
	public void setGovernorId(UUID governorId) {
		this.governorId = governorId;
	}
	
	public ColonyType getType() {
		return type;
	}
	
	public void setType(ColonyType type) {
		this.type = type;
	}
	
	public Territory getTerritory() {
		return territory;
	}
	
	public long getEstablishedDate() {
		return establishedDate;
	}
	
	// Independence mechanics
	public boolean isIndependenceRequested() {
		return independenceRequested;
	}
	
	public void requestIndependence() {
		this.independenceRequested = true;
		this.independenceRequestDate = System.currentTimeMillis();
	}
	
	public void cancelIndependenceRequest() {
		this.independenceRequested = false;
		this.independenceRequestDate = 0;
	}
	
	public boolean canRequestIndependence() {
		long colonyAge = System.currentTimeMillis() - establishedDate;
		return colonyAge >= INDEPENDENCE_REQUIREMENT_TIME && type == ColonyType.SETTLEMENT;
	}
	
	public long getIndependenceRequestDate() {
		return independenceRequestDate;
	}
}
