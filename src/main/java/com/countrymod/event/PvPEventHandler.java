package com.countrymod.event;

import com.countrymod.CountryMod;
import com.countrymod.manager.CountryManager;
import com.countrymod.model.Country;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Handles PvP events for country takeover mechanics.
 * Triggers takeover window when a leader is killed.
 */
public class PvPEventHandler implements ServerLivingEntityEvents.AfterDeath {
        @Override
        public void afterDeath(LivingEntity entity, DamageSource damageSource) {
                // Check if the killed entity is a player
                if (!(entity instanceof ServerPlayerEntity killedPlayer)) {
                        return;
                }
                
                // Check if the killer is also a player
                if (damageSource.getAttacker() instanceof ServerPlayerEntity killer) {
                        CountryManager manager = CountryMod.getCountryManager();
                        Country country = manager.getCountryByPlayer(killedPlayer.getUuid());
                        
                        // Check if killed player was a country leader
                        if (country != null && country.getLeaderId().equals(killedPlayer.getUuid())) {
                                // Start takeover window
                                manager.handleLeaderDeath(country.getCountryId(), killer.getUuid());
                                
                                // Notify players
                                String countryName = country.getName();
                                killer.sendMessage(Text.literal("You have killed the leader of " + countryName + "! Capture their flag within 5 minutes to take over!"), false);
                                
                                // Notify all citizens
                                for (ServerPlayerEntity player : killer.getServerWorld().getServer().getPlayerManager().getPlayerList()) {
                                        if (country.getCitizen(player.getUuid()) != null) {
                                                player.sendMessage(Text.literal("WARNING: Your country is under attack! Defend the flag!"), false);
                                        }
                                }
                                
                                CountryMod.LOGGER.info("Country {} is under attack by {}", countryName, killer.getName().getString());
                        }
                }
        }
}
