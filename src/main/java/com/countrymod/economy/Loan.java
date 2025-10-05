package com.countrymod.economy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan taken by a player with interest and duration.
 */
public class Loan {
	private double amount;
	private double interestRate;
	private long createdAt;
	private long lastPayment;
	private int durationDays;
	
	public Loan(double amount, double interestRate, int durationDays) {
		this.amount = amount;
		this.interestRate = interestRate;
		this.createdAt = Instant.now().getEpochSecond();
		this.lastPayment = Instant.now().getEpochSecond();
		this.durationDays = durationDays;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getInterestRate() {
		return interestRate;
	}
	
	public long getCreatedAt() {
		return createdAt;
	}
	
	public long getLastPayment() {
		return lastPayment;
	}
	
	public void setLastPayment(long lastPayment) {
		this.lastPayment = lastPayment;
	}
	
	public int getDurationDays() {
		return durationDays;
	}
	
	public long getDaysRemaining() {
		long now = Instant.now().getEpochSecond();
		long endDate = createdAt + (durationDays * 86400L);
		long remaining = ChronoUnit.DAYS.between(Instant.ofEpochSecond(now), Instant.ofEpochSecond(endDate));
		return Math.max(0, remaining);
	}
	
	public boolean isOverdue() {
		return getDaysRemaining() == 0 && amount > 0;
	}
	
	public void applyInterest() {
		long now = Instant.now().getEpochSecond();
		long days = ChronoUnit.DAYS.between(Instant.ofEpochSecond(lastPayment), Instant.ofEpochSecond(now));
		if (days > 0) {
			amount *= Math.pow(1 + interestRate, days / 30.0);
			lastPayment = now;
		}
	}
}
