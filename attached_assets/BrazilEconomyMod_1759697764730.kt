package com.brazileconomy.mod

import com.brazileconomy.mod.command.EconomyCommands
import com.brazileconomy.mod.economy.EconomyManager
import com.brazileconomy.mod.util.CPFGenerator
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BrazilEconomyMod : ModInitializer {
    companion object {
        const val MOD_ID = "brazileconomy"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
        lateinit var economyManager: EconomyManager
            private set
        
        private var tickCounter = 0
        private const val UPDATE_INTERVAL = 20 * 60 * 5 // 5 minutes in ticks
    }

    override fun onInitialize() {
        LOGGER.info("Initializing Brazil Economy Mod")
        
        economyManager = EconomyManager()
        
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            EconomyCommands.register(dispatcher)
        }
        
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val player = handler.player
            val account = economyManager.getOrCreateAccount(player.uuid, player.name.string)
            
            if (!account.hasCpf()) {
                val generatedCPF = CPFGenerator.generateCPF()
                account.cpf = generatedCPF
                economyManager.saveData()
                
                player.sendMessage(net.minecraft.text.Text.literal("§a§l✔ Bem-vindo ao Brazil Economy!"))
                player.sendMessage(net.minecraft.text.Text.literal("§fSeu CPF foi gerado automaticamente: §e$generatedCPF"))
                player.sendMessage(net.minecraft.text.Text.literal("§fVocê recebeu §2REI$ 100,00 §fde bônus inicial!"))
                player.sendMessage(net.minecraft.text.Text.literal("§7Use /saldo para ver seu saldo e /ajuda para ver os comandos"))
            }
        }
        
        ServerTickEvents.END_SERVER_TICK.register { _ ->
            tickCounter++
            if (tickCounter >= UPDATE_INTERVAL) {
                economyManager.updateAllAccounts()
                tickCounter = 0
                LOGGER.info("Updated all player accounts (loans, investments, scores)")
            }
        }
        
        ServerLifecycleEvents.SERVER_STARTED.register { _ ->
            economyManager.loadData()
            LOGGER.info("Economy data loaded successfully")
        }
        
        ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
            economyManager.saveData()
            LOGGER.info("Economy data saved successfully")
        }
        
        LOGGER.info("Brazil Economy Mod initialized successfully!")
    }
}
