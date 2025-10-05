package com.countrymod.model;

import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the territorial boundaries of a country or colony.
 * Uses chunk-based claiming system.
 */
public class Territory {
	private List<TerritoryChunk> claimedChunks;
	private int maxChunks; // Expansion limit based on citizens/economy
	
	public Territory() {
		this.claimedChunks = new ArrayList<>();
		this.maxChunks = 16; // Default starting limit
	}
	
	public List<TerritoryChunk> getClaimedChunks() {
		return claimedChunks;
	}
	
	public void addChunk(TerritoryChunk chunk) {
		if (claimedChunks.size() < maxChunks) {
			claimedChunks.add(chunk);
		}
	}
	
	public void removeChunk(TerritoryChunk chunk) {
		claimedChunks.remove(chunk);
	}
	
	public boolean isChunkClaimed(int chunkX, int chunkZ) {
		for (TerritoryChunk chunk : claimedChunks) {
			if (chunk.getChunkX() == chunkX && chunk.getChunkZ() == chunkZ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPositionInTerritory(BlockPos pos) {
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;
		return isChunkClaimed(chunkX, chunkZ);
	}
	
	public int getMaxChunks() {
		return maxChunks;
	}
	
	public void setMaxChunks(int maxChunks) {
		this.maxChunks = maxChunks;
	}
	
	public int getClaimedChunkCount() {
		return claimedChunks.size();
	}
	
	public boolean canClaimMore() {
		return claimedChunks.size() < maxChunks;
	}
}
