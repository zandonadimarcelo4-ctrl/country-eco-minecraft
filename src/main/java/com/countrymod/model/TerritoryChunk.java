package com.countrymod.model;

/**
 * Represents a single claimed chunk in a country's territory.
 */
public class TerritoryChunk {
	private int chunkX;
	private int chunkZ;
	private long claimDate;
	
	public TerritoryChunk(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.claimDate = System.currentTimeMillis();
	}
	
	public int getChunkX() {
		return chunkX;
	}
	
	public int getChunkZ() {
		return chunkZ;
	}
	
	public long getClaimDate() {
		return claimDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TerritoryChunk)) return false;
		TerritoryChunk other = (TerritoryChunk) obj;
		return chunkX == other.chunkX && chunkZ == other.chunkZ;
	}
	
	@Override
	public int hashCode() {
		return 31 * chunkX + chunkZ;
	}
}
