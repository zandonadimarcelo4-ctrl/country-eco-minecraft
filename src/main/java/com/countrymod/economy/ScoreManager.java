package com.countrymod.economy;

/**
 * Manages credit scores for players based on their financial behavior.
 */
public class ScoreManager {
	public static void updateScore(PlayerAccount account) {
		int newScore = 600; // Base score
		
		// Penalize for active loans
		double totalLoans = account.getTotalLoans();
		if (totalLoans > 0) {
			newScore -= Math.min((int)(totalLoans / 100), 150);
		}
		
		// Penalize for overdue loans
		boolean hasOverdueLoans = account.getLoans().stream().anyMatch(Loan::isOverdue);
		if (hasOverdueLoans) {
			newScore -= 100;
		}
		
		// Bonus for investments
		double totalInvestments = account.getTotalInvestments();
		if (totalInvestments > 0) {
			newScore += Math.min((int)(totalInvestments / 200), 100);
		}
		
		// Bonus for high balance
		if (account.getBalance() > 1000) {
			newScore += 50;
		} else if (account.getBalance() > 500) {
			newScore += 25;
		}
		
		// Clamp score between 300 and 850
		account.setCreditScore(Math.max(300, Math.min(850, newScore)));
	}
	
	public static String getScoreRating(int score) {
		if (score >= 800) return "EXCELLENT";
		if (score >= 700) return "VERY GOOD";
		if (score >= 600) return "GOOD";
		if (score >= 500) return "FAIR";
		if (score >= 400) return "POOR";
		return "VERY POOR";
	}
	
	public static String getScoreColor(int score) {
		if (score >= 800) return "§a§l"; // Green bold
		if (score >= 700) return "§2§l"; // Dark green bold
		if (score >= 600) return "§e§l"; // Yellow bold
		if (score >= 500) return "§6§l"; // Gold bold
		if (score >= 400) return "§c§l"; // Red bold
		return "§4§l"; // Dark red bold
	}
}
