package com.countrymod.system;

import java.util.*;

/**
 * Prison/Jail system - arrest players for crimes, they pay bail with REI$!
 * Crimes include: theft, murder, tax evasion, corruption, illegal trading.
 */
@SuppressWarnings("unused")
public class PrisonSystem {
	private Map<UUID, Prisoner> prisoners; // playerUuid -> Prisoner

	public enum Crime {
		THEFT("Theft", 500.0, 300),
		MURDER("Murder", 2000.0, 1200),
		TAX_EVASION("Tax Evasion", 1000.0, 600),
		CORRUPTION("Corruption", 3000.0, 1800),
		ILLEGAL_TRADING("Illegal Trading", 800.0, 400),
		TREASON("Treason", 5000.0, 2400);

		private final String displayName;
		private final double bailAmount;
		private final int sentenceSeconds; // Real-time seconds

		Crime(String displayName, double bailAmount, int sentenceSeconds) {
			this.displayName = displayName;
			this.bailAmount = bailAmount;
			this.sentenceSeconds = sentenceSeconds;
		}

		public String getDisplayName() {
			return displayName;
		}

		public double getBailAmount() {
			return bailAmount;
		}

		public int getSentenceSeconds() {
			return sentenceSeconds;
		}
	}

	public PrisonSystem() {
		this.prisoners = new HashMap<>();
	}

	public boolean arrestPlayer(UUID playerUuid, String playerName, Crime crime, UUID countryId) {
		if (!prisoners.containsKey(playerUuid)) {
			Prisoner prisoner = new Prisoner(playerUuid, playerName, crime, countryId);
			prisoners.put(playerUuid, prisoner);
			return true;
		}
		return false; // Already in prison
	}

	public boolean payBail(UUID playerUuid, double amount) {
		Prisoner prisoner = prisoners.get(playerUuid);
		if (prisoner != null && amount >= prisoner.crime.getBailAmount()) {
			prisoners.remove(playerUuid);
			return true;
		}
		return false;
	}

	public boolean isImprisoned(UUID playerUuid) {
		Prisoner prisoner = prisoners.get(playerUuid);
		if (prisoner != null) {
			// Check if sentence is complete
			long elapsed = (System.currentTimeMillis() - prisoner.arrestTime) / 1000;
			if (elapsed >= prisoner.crime.getSentenceSeconds()) {
				prisoners.remove(playerUuid);
				return false;
			}
			return true;
		}
		return false;
	}

	public Prisoner getPrisoner(UUID playerUuid) {
		return prisoners.get(playerUuid);
	}

	public int getRemainingTime(UUID playerUuid) {
		Prisoner prisoner = prisoners.get(playerUuid);
		if (prisoner != null) {
			long elapsed = (System.currentTimeMillis() - prisoner.arrestTime) / 1000;
			return Math.max(0, prisoner.crime.getSentenceSeconds() - (int) elapsed);
		}
		return 0;
	}

	public static class Prisoner {
		private UUID playerUuid;
		private String playerName;
		private Crime crime;
		private UUID countryId;
		private long arrestTime;

		public Prisoner(UUID playerUuid, String playerName, Crime crime, UUID countryId) {
			this.playerUuid = playerUuid;
			this.playerName = playerName;
			this.crime = crime;
			this.countryId = countryId;
			this.arrestTime = System.currentTimeMillis();
		}

		public UUID getPlayerUuid() {
			return playerUuid;
		}

		public String getPlayerName() {
			return playerName;
		}

		public Crime getCrime() {
			return crime;
		}

		public UUID getCountryId() {
			return countryId;
		}

		public long getArrestTime() {
			return arrestTime;
		}
	}
}
