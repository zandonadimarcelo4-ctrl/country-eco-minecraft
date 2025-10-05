package com.countrymod.system;

import com.countrymod.model.Country;
import com.countrymod.model.Citizen;
import java.util.*;

/**
 * Corruption system where leaders can accept bribes and citizens can report corruption.
 * High corruption leads to revolutions and overthrows!
 */
@SuppressWarnings("unused")
public class CorruptionSystem {
	private Map<UUID, CorruptionProfile> countryCorruption;
	private static final double REVOLUTION_THRESHOLD = 80.0;
	
	public CorruptionSystem() {
		this.countryCorruption = new HashMap<>();
	}
	
	public CorruptionProfile getProfile(UUID countryId) {
		return countryCorruption.computeIfAbsent(countryId, k -> new CorruptionProfile());
	}
	
	public boolean acceptBribe(UUID countryId, UUID fromPlayer, double amount) {
		CorruptionProfile profile = getProfile(countryId);
		profile.addBribe(fromPlayer, amount);
		profile.corruptionLevel += amount / 100.0; // Corruption increases
		
		return profile.corruptionLevel < 100.0;
	}
	
	public void reportCorruption(UUID countryId, UUID reporterUuid) {
		CorruptionProfile profile = getProfile(countryId);
		profile.addReport(reporterUuid);
		
		// Each report reduces corruption by 5%
		profile.corruptionLevel = Math.max(0, profile.corruptionLevel - 5.0);
	}
	
	public boolean shouldTriggerRevolution(UUID countryId) {
		CorruptionProfile profile = getProfile(countryId);
		return profile.corruptionLevel >= REVOLUTION_THRESHOLD;
	}
	
	public static class CorruptionProfile {
		private double corruptionLevel; // 0-100
		private List<Bribe> bribes;
		private List<Report> reports;
		
		public CorruptionProfile() {
			this.corruptionLevel = 0.0;
			this.bribes = new ArrayList<>();
			this.reports = new ArrayList<>();
		}
		
		public void addBribe(UUID fromPlayer, double amount) {
			bribes.add(new Bribe(fromPlayer, amount, System.currentTimeMillis()));
		}
		
		public void addReport(UUID reporterUuid) {
			reports.add(new Report(reporterUuid, System.currentTimeMillis()));
		}
		
		public double getCorruptionLevel() {
			return corruptionLevel;
		}
		
		public String getCorruptionRating() {
			if (corruptionLevel < 20) return "§aClean";
			if (corruptionLevel < 40) return "§eSlight";
			if (corruptionLevel < 60) return "§6Moderate";
			if (corruptionLevel < 80) return "§cHigh";
			return "§4§lCRITICAL - REVOLUTION IMMINENT!";
		}
	}
	
	public static class Bribe {
		private UUID fromPlayer;
		private double amount;
		private long timestamp;
		
		public Bribe(UUID fromPlayer, double amount, long timestamp) {
			this.fromPlayer = fromPlayer;
			this.amount = amount;
			this.timestamp = timestamp;
		}
	}
	
	public static class Report {
		private UUID reporterUuid;
		private long timestamp;
		
		public Report(UUID reporterUuid, long timestamp) {
			this.reporterUuid = reporterUuid;
			this.timestamp = timestamp;
		}
	}
}
