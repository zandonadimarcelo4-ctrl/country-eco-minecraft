package com.countrymod;

import com.countrymod.block.ModBlocks;
import com.countrymod.command.ModCommands;
import com.countrymod.event.PvPEventHandler;
import com.countrymod.item.ModItems;
import com.countrymod.manager.CountryManager;
import com.countrymod.manager.DataPersistence;
import com.countrymod.manager.EconomyManager;
import com.countrymod.system.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryMod implements ModInitializer {
    public static final String MOD_ID = "countrymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static CountryManager countryManager;
    private static DataPersistence dataPersistence;
    private static EconomyManager economyManager;
    private static RankSystem rankSystem;
    private static TaxSystem taxSystem;
    private static TradeSystem tradeSystem;
    private static WarSystem warSystem;
    private static AllianceSystem allianceSystem;
    private static SecureTradeSystem secureTradeSystem;
    private static MinecraftServer server;

    private static int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 20 * 60 * 5; // 5 minutos

    @Override
    public void onInitialize() {
        LOGGER.info("Iniciando CountryMod");

        // Inicializar managers
        countryManager = new CountryManager();
        dataPersistence = new DataPersistence();
        economyManager = new EconomyManager();
        rankSystem = new RankSystem();
        taxSystem = new TaxSystem();
        tradeSystem = new TradeSystem();
        warSystem = new WarSystem();
        allianceSystem = new AllianceSystem();
        secureTradeSystem = new SecureTradeSystem();

        // Registrar itens e blocos
        ModItems.register();
        ModBlocks.register();

        // Registrar eventos
        registerEventHandlers();

        // Tick econômico
        ServerTickEvents.END_SERVER_TICK.register(srv -> {
            tickCounter++;
            if (tickCounter >= UPDATE_INTERVAL) {
                economyManager.updateAllAccounts();
                tickCounter = 0;
                LOGGER.info("Contas de todos os jogadores atualizadas");
            }
        });

        // Eventos de servidor
        ServerLifecycleEvents.SERVER_STARTED.register(srv -> {
            server = srv;
            LOGGER.info("Servidor iniciado - carregando dados");
            dataPersistence.loadData(srv);
            ModCommands.register();
            LOGGER.info("Comandos do CountryMod registrados");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(srv -> {
            LOGGER.info("Servidor parando - salvando dados");
            dataPersistence.saveData(srv);
        });

        LOGGER.info("CountryMod inicializado com sucesso");
    }

    private void registerEventHandlers() {
        ServerLivingEntityEvents.AFTER_DEATH.register(new PvPEventHandler());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, srv) -> {
            var player = handler.getPlayer();
            var account = economyManager.getOrCreateAccount(player.getUuid(), player.getName().getString());
            if (account.hasCpf()) {
                player.sendMessage(
                    net.minecraft.text.Text.literal("§aBem-vindo! Seu CPF: §e" + account.getCpf()), false);
                player.sendMessage(
                    net.minecraft.text.Text.literal("§fSaldo: §2REI$ " + String.format("%.2f", account.getBalance())), false);
            }
        });
    }

    // GETTERS
    public static CountryManager getCountryManager() { return countryManager; }
    public static DataPersistence getDataPersistence() { return dataPersistence; }
    public static EconomyManager getEconomyManager() { return economyManager; }
    public static RankSystem getRankSystem() { return rankSystem; }
    public static TaxSystem getTaxSystem() { return taxSystem; }
    public static TradeSystem getTradeSystem() { return tradeSystem; }
    public static WarSystem getWarSystem() { return warSystem; }
    public static AllianceSystem getAllianceSystem() { return allianceSystem; }
    public static SecureTradeSystem getSecureTradeSystem() { return secureTradeSystem; }
    public static MinecraftServer getServer() { return server; }
}
