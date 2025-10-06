package com.countrymod.command;

import com.countrymod.CountryMod;
import com.countrymod.manager.EconomyManager;
import com.countrymod.system.RankSystem;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("countrymod")
                .then(CommandManager.literal("rank").executes(ModCommands::showRankLeaderboard))
                .then(CommandManager.literal("money").executes(ModCommands::showMoneyLeaderboard))
                .then(CommandManager.literal("score").executes(ModCommands::showCreditScoreLeaderboard))
                .then(CommandManager.literal("balance").executes(ModCommands::showBalance))
                .then(CommandManager.literal("cpf").executes(ModCommands::showCPF))
                .then(CommandManager.literal("info").executes(ModCommands::showCountryInfo))
                .then(CommandManager.literal("list").executes(ModCommands::listAllCountries))
                .then(CommandManager.literal("prisoners").executes(ModCommands::listPrisoners))
                .then(CommandManager.literal("bounties").executes(ModCommands::listBounties))
            );
        });
    }

    private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> ctx) {
        return ctx.getSource().getPlayer();
    }

    public static int showRankLeaderboard(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        var rank = CountryMod.getRankSystem().getRank(player.getUuid());
        player.sendMessage(Text.literal("Rank: " + rank.getCurrentRank().getColoredName() +
                " | XP: " + rank.getTotalXP() + " | Progress: " + String.format("%.2f", rank.getProgressToNextRank()) + "%"), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int showMoneyLeaderboard(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        double balance = CountryMod.getEconomyManager().getBalance(player.getUuid());
        player.sendMessage(Text.literal("Money: REI$ " + String.format("%.2f", balance)), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int showCreditScoreLeaderboard(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        int score = CountryMod.getEconomyManager().getCreditScore(player.getUuid());
        player.sendMessage(Text.literal("Credit Score: " + score), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int showBalance(CommandContext<ServerCommandSource> ctx) {
        return showMoneyLeaderboard(ctx);
    }

    public static int showCPF(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        var cpf = CountryMod.getEconomyManager().getOrCreateAccount(player.getUuid(), player.getName().getString()).getCpf();
        player.sendMessage(Text.literal("CPF: " + cpf), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int showCountryInfo(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        var country = CountryMod.getCountryManager().getCountryByPlayer(player.getUuid());
        if (country == null) {
            player.sendMessage(Text.literal("Você não pertence a nenhum país."), false);
            return Command.SINGLE_SUCCESS;
        }
        player.sendMessage(Text.literal("País: " + country.getName() +
                " | Governante: " + country.getLeaderName() +
                " | População: " + country.getPopulation()), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int listAllCountries(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        CountryMod.getCountryManager().getCountriesMap().values().forEach(country -> {
            player.sendMessage(Text.literal("País: " + country.getName() + " | Governante: " + country.getLeaderName()), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int listPrisoners(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        CountryMod.getCountryManager().getPrisoners().forEach(prisoner -> {
            player.sendMessage(Text.literal("Prisioneiro: " + prisoner.getName() + " | País: " + prisoner.getCountryName()), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int listBounties(CommandContext<ServerCommandSource> ctx) {
        var player = getPlayer(ctx);
        CountryMod.getCountryManager().getBounties().forEach(bounty -> {
            player.sendMessage(Text.literal("Bounty: " + bounty.getTargetName() + " | Valor: REI$ " + String.format("%.2f", bounty.getAmount())), false);
        });
        return Command.SINGLE_SUCCESS;
    }
}
