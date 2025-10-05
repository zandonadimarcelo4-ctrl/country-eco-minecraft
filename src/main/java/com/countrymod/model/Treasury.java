package com.countrymod.model;

import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages a country's economic resources and treasury.
 * Placeholder for full economy system implementation.
 */
public class Treasury {
	private Map<String, Integer> resources; // Resource type -> quantity
	private int gold; // Primary currency
	private double taxRate; // Tax rate for citizens
	
	public Treasury() {
		this.resources = new HashMap<>();
		this.gold = 0;
		this.taxRate = 0.1; // 10% default tax rate
	}
	
	public Map<String, Integer> getResources() {
		return resources;
	}
	
	public void addResource(String resourceType, int amount) {
		resources.put(resourceType, resources.getOrDefault(resourceType, 0) + amount);
	}
	
	public boolean removeResource(String resourceType, int amount) {
		int current = resources.getOrDefault(resourceType, 0);
		if (current >= amount) {
			resources.put(resourceType, current - amount);
			return true;
		}
		return false;
	}
	
	public int getGold() {
		return gold;
	}
	
	public void addGold(int amount) {
		this.gold += amount;
	}
	
	public boolean removeGold(int amount) {
		if (gold >= amount) {
			gold -= amount;
			return true;
		}
		return false;
	}
	
	public double getTaxRate() {
		return taxRate;
	}
	
	public void setTaxRate(double taxRate) {
		this.taxRate = Math.max(0.0, Math.min(1.0, taxRate)); // Clamp between 0-100%
	}
	
	/**
	 * Placeholder for economy expansion:
	 * - Trade with other countries
	 * - Salaries for military
	 * - Resource generation from colonies
	 */
}
