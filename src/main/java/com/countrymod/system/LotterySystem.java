package com.countrymod.system;

import java.util.*;

/**
 * National Lottery system - buy tickets with REI$ and win big!
 * Countries can run lotteries to generate revenue.
 */
public class LotterySystem {
	private Map<UUID, Lottery> activeLotteries; // countryId -> Lottery
	
	public LotterySystem() {
		this.activeLotteries = new HashMap<>();
	}
	
	public Lottery createLottery(UUID countryId, double ticketPrice, double jackpot) {
		Lottery lottery = new Lottery(countryId, ticketPrice, jackpot);
		activeLotteries.put(countryId, lottery);
		return lottery;
	}
	
	public boolean buyTicket(UUID countryId, UUID playerUuid, String playerName) {
		Lottery lottery = activeLotteries.get(countryId);
		if (lottery != null && !lottery.isDrawn()) {
			return lottery.addTicket(playerUuid, playerName);
		}
		return false;
	}
	
	public LotteryResult drawWinner(UUID countryId) {
		Lottery lottery = activeLotteries.get(countryId);
		if (lottery != null) {
			return lottery.draw();
		}
		return null;
	}
	
	public static class Lottery {
		private UUID countryId;
		private double ticketPrice;
		private double jackpot;
		private List<LotteryTicket> tickets;
		private boolean drawn;
		private LotteryResult result;
		
		public Lottery(UUID countryId, double ticketPrice, double jackpot) {
			this.countryId = countryId;
			this.ticketPrice = ticketPrice;
			this.jackpot = jackpot;
			this.tickets = new ArrayList<>();
			this.drawn = false;
		}
		
		public boolean addTicket(UUID playerUuid, String playerName) {
			if (!drawn) {
				tickets.add(new LotteryTicket(playerUuid, playerName));
				jackpot += ticketPrice * 0.8; // 80% goes to jackpot, 20% to country
				return true;
			}
			return false;
		}
		
		public LotteryResult draw() {
			if (drawn || tickets.isEmpty()) {
				return null;
			}
			
			Random random = new Random();
			LotteryTicket winner = tickets.get(random.nextInt(tickets.size()));
			
			result = new LotteryResult(winner.playerUuid, winner.playerName, jackpot);
			drawn = true;
			return result;
		}
		
		public boolean isDrawn() {
			return drawn;
		}
		
		public double getTicketPrice() {
			return ticketPrice;
		}
		
		public double getJackpot() {
			return jackpot;
		}
		
		public int getTicketCount() {
			return tickets.size();
		}
	}
	
	public static class LotteryTicket {
		private UUID playerUuid;
		private String playerName;
		
		public LotteryTicket(UUID playerUuid, String playerName) {
			this.playerUuid = playerUuid;
			this.playerName = playerName;
		}
	}
	
	public static class LotteryResult {
		private UUID winnerUuid;
		private String winnerName;
		private double prizeAmount;
		
		public LotteryResult(UUID winnerUuid, String winnerName, double prizeAmount) {
			this.winnerUuid = winnerUuid;
			this.winnerName = winnerName;
			this.prizeAmount = prizeAmount;
		}
		
		public UUID getWinnerUuid() { return winnerUuid; }
		public String getWinnerName() { return winnerName; }
		public double getPrizeAmount() { return prizeAmount; }
	}
}
