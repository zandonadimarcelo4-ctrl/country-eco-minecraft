package com.countrymod.command;

import com.countrymod.CountryMod;
import com.countrymod.system.RankSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.*;
import java.util.stream.Collectors;

/**
 * All mod commands - ranks, leaderboards, economy, countries, etc.
 */
public class ModCommands {
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		registerRankCommands(dispatcher);
		registerLeaderboardCommands(dispatcher);
		registerEconomyCommands(dispatcher);
		registerCountryCommands(dispatcher);
		registerPrisonCommands(dispatcher);
		registerBountyCommands(dispatcher);
	}
	
	private static void registerRankCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("rank")
				.executes(ModCommands::showMyRank)
		);
		
		dispatcher.register(
			CommandManager.literal("ranks")
				.executes(ModCommands::showAllRanks)
		);
		
		dispatcher.register(
			CommandManager.literal("xp")
				.executes(ModCommands::showMyXP)
		);
	}
	
	private static void registerLeaderboardCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("leaderboard")
				.then(CommandManager.literal("rank")
					.executes(ModCommands::showRankLeaderboard))
				.then(CommandManager.literal("money")
					.executes(ModCommands::showMoneyLeaderboard))
				.then(CommandManager.literal("score")
					.executes(ModCommands::showCreditScoreLeaderboard))
		);
		
		dispatcher.register(
			CommandManager.literal("top")
				.executes(ModCommands::showRankLeaderboard)
		);
	}
	
	private static void registerEconomyCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("balance")
				.executes(ModCommands::showBalance)
		);
		
		dispatcher.register(
			CommandManager.literal("cpf")
				.executes(ModCommands::showCPF)
		);
		
		dispatcher.register(
			CommandManager.literal("wallet")
				.executes(ModCommands::showWallet)
		);
	}
	
	private static void registerCountryCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("country")
				.then(CommandManager.literal("info")
					.executes(ModCommands::showCountryInfo))
				.then(CommandManager.literal("list")
					.executes(ModCommands::listAllCountries))
		);
	}
	
	private static void registerPrisonCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("prison")
				.then(CommandManager.literal("list")
					.executes(ModCommands::listPrisoners))
		);
	}
	
	private static void registerBountyCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("bounties")
				.executes(ModCommands::listBounties)
		);
		
		dispatcher.register(
			CommandManager.literal("wanted")
				.executes(ModCommands::listBounties)
		);
	}
	
	// RANK COMMANDS
	private static int showMyRank(CommandContext<ServerCommandSource> context) {
		final ServerPlayerEntity player;
		try {
			player = context.getSource().getPlayerOrThrow();
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
			context.getSource().sendFeedback(() -> Text.literal("§cThis command can only be run by a player."), false);
			return 0;
		}
		var rankSystem = CountryMod.getRankSystem();
		var playerRank = rankSystem.getRank(player.getUuid());
		var rank = playerRank.getCurrentRank();
		
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
	context.getSource().sendFeedback(() -> Text.literal("§6§l YOUR RANK"), false);
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
	context.getSource().sendFeedback(() -> Text.literal("§fRank: " + rank.getColoredName()), false);
	context.getSource().sendFeedback(() -> Text.literal("§fTotal XP: §b" + playerRank.getTotalXP()), false);
	context.getSource().sendFeedback(() -> Text.literal("§fProgress: §a" + String.format("%.1f%%", playerRank.getProgressToNextRank())), false);
	context.getSource().sendFeedback(() -> Text.literal("§fXP to Next: §e" + playerRank.getXPToNextRank()), false);
	context.getSource().sendFeedback(() -> Text.literal("§fPerk: §d" + rank.getPerk()), false);
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		
		return 1;
	}
	
	private static int showAllRanks(CommandContext<ServerCommandSource> context) {
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
	context.getSource().sendFeedback(() -> Text.literal("§6§l RANK PROGRESSION"), false);
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		
		int position = 1;
		for (RankSystem.Rank rank : RankSystem.Rank.values()) {
			final int pos = position;
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("§f%d. %s §7(%,d XP) §f- §a%s", 
					pos, rank.getColoredName(), rank.getXpRequired(), rank.getPerk())
			), false);
			position++;
		}
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		return 1;
	}
	
	private static int showMyXP(CommandContext<ServerCommandSource> context) {
		final ServerPlayerEntity player;
		try {
			player = context.getSource().getPlayerOrThrow();
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
			context.getSource().sendFeedback(() -> Text.literal("§cThis command can only be run by a player."), false);
			return 0;
		}
		var rankSystem = CountryMod.getRankSystem();
		var playerRank = rankSystem.getRank(player.getUuid());
		
		context.getSource().sendFeedback(() -> Text.literal(
			String.format("§6[XP] §fYou have §b%,d XP §f(§a%.1f%% §fto next rank)", 
				playerRank.getTotalXP(), playerRank.getProgressToNextRank())
		), false);
		
		return 1;
	}
	
	// LEADERBOARD COMMANDS
	private static int showRankLeaderboard(CommandContext<ServerCommandSource> context) {
	var rankSystem = CountryMod.getRankSystem();
	var server = context.getSource().getServer();
		
		// Get all player ranks and sort
		List<Map.Entry<String, Integer>> rankings = new ArrayList<>();
		for (var player : server.getPlayerManager().getPlayerList()) {
			var rank = rankSystem.getRank(player.getUuid());
			rankings.add(Map.entry(player.getName().getString(), rank.getTotalXP()));
		}
		rankings.sort((a, b) -> b.getValue().compareTo(a.getValue()));
		
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
	context.getSource().sendFeedback(() -> Text.literal("§6§l TOP 10 PLAYERS (BY RANK)"), false);
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		
		int position = 1;
		for (var entry : rankings.stream().limit(10).collect(Collectors.toList())) {
			final int pos = position;
			final String name = entry.getKey();
			final int xp = entry.getValue();
			final var rank = RankSystem.Rank.fromXP(xp);
			
			String medal = pos == 1 ? "§6🥇" : pos == 2 ? "§f🥈" : pos == 3 ? "§c🥉" : "§7#" + pos;
			
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("%s §f%s §7- %s §b(%,d XP)", medal, name, rank.getColoredName(), xp)
			), false);
			position++;
		}
		
	context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		return 1;
	}
	
	private static int showMoneyLeaderboard(CommandContext<ServerCommandSource> context) {
		var economyManager = CountryMod.getEconomyManager();
		var server = context.getSource().getServer();
		
		List<Map.Entry<String, Double>> rankings = new ArrayList<>();
		for (var player : server.getPlayerManager().getPlayerList()) {
			var account = economyManager.getAccount(player.getUuid());
			if (account != null) {
				rankings.add(Map.entry(player.getName().getString(), account.getBalance()));
			}
		}
		rankings.sort((a, b) -> b.getValue().compareTo(a.getValue()));
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		context.getSource().sendFeedback(() -> Text.literal("§2§l TOP 10 RICHEST PLAYERS"), false);
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		
		int position = 1;
		for (var entry : rankings.stream().limit(10).collect(Collectors.toList())) {
			final int pos = position;
			final String name = entry.getKey();
			final double balance = entry.getValue();
			
			String medal = pos == 1 ? "§6🥇" : pos == 2 ? "§f🥈" : pos == 3 ? "§c🥉" : "§7#" + pos;
			
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("%s §f%s §7- §2REI$ %,.2f", medal, name, balance)
			), false);
			position++;
		}
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		return 1;
	}
	
	private static int showCreditScoreLeaderboard(CommandContext<ServerCommandSource> context) {
		var economyManager = CountryMod.getEconomyManager();
		var server = context.getSource().getServer();
		
		List<Map.Entry<String, Integer>> rankings = new ArrayList<>();
		for (var player : server.getPlayerManager().getPlayerList()) {
			var account = economyManager.getAccount(player.getUuid());
			if (account != null) {
				rankings.add(Map.entry(player.getName().getString(), account.getCreditScore()));
			}
		}
		rankings.sort((a, b) -> b.getValue().compareTo(a.getValue()));
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		context.getSource().sendFeedback(() -> Text.literal("§a§l TOP 10 CREDIT SCORES"), false);
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		
		int position = 1;
		for (var entry : rankings.stream().limit(10).collect(Collectors.toList())) {
			final int pos = position;
			final String name = entry.getKey();
			final int score = entry.getValue();
			
			String medal = pos == 1 ? "§6🥇" : pos == 2 ? "§f🥈" : pos == 3 ? "§c🥉" : "§7#" + pos;
			
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("%s §f%s §7- §aScore: %d", medal, name, score)
			), false);
			position++;
		}
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		return 1;
	}
	
	// ECONOMY COMMANDS
	private static int showBalance(CommandContext<ServerCommandSource> context) {
		final ServerPlayerEntity player;
		try {
			player = context.getSource().getPlayerOrThrow();
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
			context.getSource().sendFeedback(() -> Text.literal("§cThis command can only be run by a player."), false);
			return 0;
		}
		var account = CountryMod.getEconomyManager().getAccount(player.getUuid());
		
		if (account != null) {
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("§2[BANK] §fBalance: §2REI$ %,.2f", account.getBalance())
			), false);
		}
		
		return 1;
	}
	
	private static int showCPF(CommandContext<ServerCommandSource> context) {
		final ServerPlayerEntity player;
		try {
			player = context.getSource().getPlayerOrThrow();
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
			context.getSource().sendFeedback(() -> Text.literal("§cThis command can only be run by a player."), false);
			return 0;
		}
		var account = CountryMod.getEconomyManager().getAccount(player.getUuid());
		
		if (account != null && account.hasCpf()) {
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("§a[CPF] §fYour CPF: §e%s", account.getCpf())
			), false);
		}
		
		return 1;
	}
	
	private static int showWallet(CommandContext<ServerCommandSource> context) {
		final ServerPlayerEntity player;
		try {
			player = context.getSource().getPlayerOrThrow();
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
			context.getSource().sendFeedback(() -> Text.literal("§cThis command can only be run by a player."), false);
			return 0;
		}
		var account = CountryMod.getEconomyManager().getAccount(player.getUuid());
		
		if (account != null) {
			context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
			context.getSource().sendFeedback(() -> Text.literal("§2§l YOUR WALLET"), false);
			context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
			context.getSource().sendFeedback(() -> Text.literal(String.format("§fCPF: §e%s", account.getCpf())), false);
			context.getSource().sendFeedback(() -> Text.literal(String.format("§fBalance: §2REI$ %,.2f", account.getBalance())), false);
			context.getSource().sendFeedback(() -> Text.literal(String.format("§fCredit Score: §a%d", account.getCreditScore())), false);
			context.getSource().sendFeedback(() -> Text.literal(String.format("§fLoans: §c%,.2f", account.getTotalLoans())), false);
			context.getSource().sendFeedback(() -> Text.literal(String.format("§fInvestments: §a%,.2f", account.getTotalInvestments())), false);
			context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		}
		
		return 1;
	}
	
	// COUNTRY COMMANDS
	private static int showCountryInfo(CommandContext<ServerCommandSource> context) {
		final ServerPlayerEntity player;
		try {
			player = context.getSource().getPlayerOrThrow();
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
			context.getSource().sendFeedback(() -> Text.literal("§cThis command can only be run by a player."), false);
			return 0;
		}
		var country = CountryMod.getCountryManager().getCountryByPlayer(player.getUuid());
		
		if (country != null) {
			context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
			context.getSource().sendFeedback(() -> Text.literal("§6§l " + country.getName()), false);
			context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
			context.getSource().sendFeedback(() -> Text.literal("§fGovernment: §a" + country.getGovernmentType().getDisplayName()), false);
			context.getSource().sendFeedback(() -> Text.literal("§fCitizens: §b" + country.getCitizens().size()), false);
			context.getSource().sendFeedback(() -> Text.literal("§fMilitary: §c" + country.getMilitaryMembers().size()), false);
			context.getSource().sendFeedback(() -> Text.literal("§fColonies: §d" + country.getColonies().size()), false);
			context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		} else {
			context.getSource().sendFeedback(() -> Text.literal("§c[ERROR] §fYou are not in a country!"), false);
		}
		
		return 1;
	}
	
	private static int listAllCountries(CommandContext<ServerCommandSource> context) {
		var countries = CountryMod.getCountryManager().getAllCountries();
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		context.getSource().sendFeedback(() -> Text.literal("§6§l ALL COUNTRIES"), false);
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		
		for (var country : countries) {
			context.getSource().sendFeedback(() -> Text.literal(
				String.format("§6⚑ §f%s §7(%s) - §b%d citizens", 
					country.getName(), 
					country.getGovernmentType().getDisplayName(),
					country.getCitizens().size())
			), false);
		}
		
		context.getSource().sendFeedback(() -> Text.literal("§e§l═══════════════════════════"), false);
		return 1;
	}
	
	// PRISON COMMANDS
	private static int listPrisoners(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback(() -> Text.literal("§c[PRISON] §fPrisoner list feature coming soon!"), false);
		return 1;
	}
	
	// BOUNTY COMMANDS
	private static int listBounties(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback(() -> Text.literal("§c[WANTED] §fBounty list feature coming soon!"), false);
		return 1;
	}
}
