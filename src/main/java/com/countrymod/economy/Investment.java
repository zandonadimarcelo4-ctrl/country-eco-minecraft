package com.countrymod.economy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Represents an investment made by a player that grows over time.
 */
public class Investment {
	private double amount;
	private double rate;
	private long start;
	private long lastUpdate;
	
	public Investment(double amount, double rate) {
		this.amount = amount;
		this.rate = rate;
		this.start = Instant.now().getEpochSecond();
		this.lastUpdate = Instant.now().getEpochSecond();
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getRate() {
		return rate;
	}
	
	public long getStart() {
		return start;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public long getDaysInvested() {
		long now = Instant.now().getEpochSecond();
		return ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(now));
	}
	
	public double getCurrentValue() {
		long now = Instant.now().getEpochSecond();
		long days = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(now));
		return amount * Math.pow(1 + rate, days / 30.0);
	}
	
	public void updateValue() {
		long now = Instant.now().getEpochSecond();
		long days = ChronoUnit.DAYS.between(Instant.ofEpochSecond(lastUpdate), Instant.ofEpochSecond(now));
		if (days > 0) {
			amount *= Math.pow(1 + rate, days / 30.0);
			lastUpdate = now;
		}
	}
}
