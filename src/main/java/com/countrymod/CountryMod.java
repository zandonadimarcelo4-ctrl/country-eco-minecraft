package com.countrymod;

import com.countrymod.block.ModBlocks;
import com.countrymod.event.PvPEventHandler;
import com.countrymod.item.ModItems;
import com.countrymod.manager.CountryManager;
import com.countrymod.manager.DataPersistence;
import com.countrymod.manager.EconomyManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryMod implements ModInitializer {
        public static final String MOD_ID = "countrymod";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        
        private static CountryManager countryManager;
        private static DataPersistence dataPersistence;
        private static EconomyManager economyManager;
        private static com.countrymod.system.RankSystem rankSystem;
        private static int tickCounter = 0;
        private static final int UPDATE_INTERVAL = 20 * 60 * 5; // 5 minutes in ticks

        @Override
        public void onInitialize() {
                LOGGER.info("Initializing Country System Mod with Economy");
                
                // Initialize managers
                countryManager = new CountryManager();
                dataPersistence = new DataPersistence();
                economyManager = new EconomyManager();
                // Initialize rank system
                rankSystem = new com.countrymod.system.RankSystem();
                
                // Register items and blocks
                ModItems.register();
                ModBlocks.register();
                
                // Register event handlers
                registerEventHandlers();
                
                // Register server tick event for economy updates
                ServerTickEvents.END_SERVER_TICK.register(server -> {
                        tickCounter++;
                        if (tickCounter >= UPDATE_INTERVAL) {
                                economyManager.updateAllAccounts();
                                tickCounter = 0;
                                LOGGER.info("Updated all player accounts (loans, investments, scores)");
                        }
                });
                
                // Register server lifecycle events
                ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                        LOGGER.info("Server started - Loading country and economy data");
                        dataPersistence.loadData(server);
                });
                
                ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
                        LOGGER.info("Server stopping - Saving country and economy data");
                        dataPersistence.saveData(server);
                });
                
                LOGGER.info("Country System Mod with Economy initialized successfully");
        }
        
        private void registerEventHandlers() {
                // Register PvP event handler for takeover mechanics
                ServerLivingEntityEvents.AFTER_DEATH.register(new PvPEventHandler());
                
                // Register player connection events
                ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                        var player = handler.getPlayer();
                        LOGGER.info("Player {} joined the server", player.getName().getString());
                        
                        // Create/get player account with CPF
                        var account = economyManager.getOrCreateAccount(player.getUuid(), player.getName().getString());
                        
                        // Send welcome message with CPF
                        if (account.hasCpf()) {
                                player.sendMessage(net.minecraft.text.Text.literal("§aWelcome! Your CPF: §e" + account.getCpf()), false);
                                player.sendMessage(net.minecraft.text.Text.literal("§fBalance: §2REI$ " + String.format("%.2f", account.getBalance())), false);
                        }
                });
                
                ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                        LOGGER.info("Player {} left the server", handler.getPlayer().getName().getString());
                });
        }
        
        public static CountryManager getCountryManager() {
                return countryManager;
        }
        
        public static DataPersistence getDataPersistence() {
                return dataPersistence;
        }
        
        public static EconomyManager getEconomyManager() {
                return economyManager;
        }

        public static com.countrymod.system.RankSystem getRankSystem() {
                return rankSystem;
        }
}
