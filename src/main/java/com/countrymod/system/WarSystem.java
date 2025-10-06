package com.countrymod.system;

import com.countrymod.CountryMod;
import com.countrymod.model.Country;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * War declaration and management system between countries
 */
public class WarSystem {
    private final Map<String, War> activeWars = new HashMap<>();
    private final Map<String, Long> warCooldowns = new HashMap<>();
    
    public static class War {
        private final String warId;
        private final String attackerCountryId;
        private final String defenderCountryId;
        private final long startTime;
        private int attackerScore;
        private int defenderScore;
        private boolean active;
        
        public War(String attackerCountryId, String defenderCountryId) {
            this.warId = UUID.randomUUID().toString();
            this.attackerCountryId = attackerCountryId;
            this.defenderCountryId = defenderCountryId;
            this.startTime = System.currentTimeMillis();
            this.attackerScore = 0;
            this.defenderScore = 0;
            this.active = true;
        }
        
        public String getWarId() { return warId; }
        public String getAttackerCountryId() { return attackerCountryId; }
        public String getDefenderCountryId() { return defenderCountryId; }
        public long getStartTime() { return startTime; }
        public int getAttackerScore() { return attackerScore; }
        public int getDefenderScore() { return defenderScore; }
        public boolean isActive() { return active; }
        
        public void addAttackerScore(int points) { attackerScore += points; }
        public void addDefenderScore(int points) { defenderScore += points; }
        public void end() { active = false; }
    }
    
    public boolean declareWar(Country attacker, Country defender) {
        if (attacker.getCountryId().equals(defender.getCountryId())) {
            return false;
        }
        
        String cooldownKey = attacker.getCountryId() + ":" + defender.getCountryId();
        if (warCooldowns.containsKey(cooldownKey)) {
            long lastWar = warCooldowns.get(cooldownKey);
            long cooldownPeriod = 48 * 60 * 60 * 1000; // 48 hours
            if (System.currentTimeMillis() - lastWar < cooldownPeriod) {
                return false;
            }
        }
        
        // Check if war already exists
        for (War war : activeWars.values()) {
            if (war.isActive() && 
                ((war.getAttackerCountryId().equals(attacker.getCountryId()) && 
                  war.getDefenderCountryId().equals(defender.getCountryId())) ||
                 (war.getAttackerCountryId().equals(defender.getCountryId()) && 
                  war.getDefenderCountryId().equals(attacker.getCountryId())))) {
                return false;
            }
        }
        
        War war = new War(attacker.getCountryId().toString(), defender.getCountryId().toString());
        activeWars.put(war.getWarId(), war);
        
        // Notify all players
        notifyAllPlayers("§c§l[WAR] §f" + attacker.getName() + " §chas declared WAR on §f" + defender.getName() + "§c!");
        
        CountryMod.LOGGER.info("War declared: {} vs {}", attacker.getName(), defender.getName());
        return true;
    }
    
    public void recordKill(UUID killerUuid, UUID victimUuid) {
        var countryManager = CountryMod.getCountryManager();
        Country killerCountry = countryManager.getCountryByPlayer(killerUuid);
        Country victimCountry = countryManager.getCountryByPlayer(victimUuid);
        
        if (killerCountry == null || victimCountry == null) {
            return;
        }
        
        for (War war : activeWars.values()) {
            if (!war.isActive()) continue;
            
            if (war.getAttackerCountryId().equals(killerCountry.getCountryId()) && 
                war.getDefenderCountryId().equals(victimCountry.getCountryId())) {
                war.addAttackerScore(1);
                checkWarEnd(war);
                return;
            } else if (war.getDefenderCountryId().equals(killerCountry.getCountryId()) && 
                       war.getAttackerCountryId().equals(victimCountry.getCountryId())) {
                war.addDefenderScore(1);
                checkWarEnd(war);
                return;
            }
        }
    }
    
    private void checkWarEnd(War war) {
        int scoreLimit = 50; // First to 50 kills wins
        
        if (war.getAttackerScore() >= scoreLimit) {
            endWar(war, war.getAttackerCountryId());
        } else if (war.getDefenderScore() >= scoreLimit) {
            endWar(war, war.getDefenderCountryId());
        }
    }
    
    public void endWar(War war, String winnerId) {
        if (!war.isActive()) return;
        
        war.end();
        var countryManager = CountryMod.getCountryManager();
        Country winner = countryManager.getCountry(UUID.fromString(winnerId));
        Country loser;
        
        if (winnerId.equals(war.getAttackerCountryId())) {
            loser = countryManager.getCountry(UUID.fromString(war.getDefenderCountryId()));
        } else {
            loser = countryManager.getCountry(UUID.fromString(war.getAttackerCountryId()));
        }
        
        if (winner != null && loser != null) {
            // Transfer 25% of loser's treasury to winner
            double spoils = loser.getTreasuryBalance() * 0.25;
            loser.withdrawFromTreasury(spoils);
            winner.addToTreasury(spoils);
            
            notifyAllPlayers("§6§l[WAR END] §f" + winner.getName() + " §ahas WON the war against §f" + 
                loser.getName() + "§a! Spoils: REI$ " + String.format("%.2f", spoils));
        }
        
        String cooldownKey = war.getAttackerCountryId() + ":" + war.getDefenderCountryId();
        warCooldowns.put(cooldownKey, System.currentTimeMillis());
    }
    
    public List<War> getActiveWars() {
        List<War> wars = new ArrayList<>();
        for (War war : activeWars.values()) {
            if (war.isActive()) {
                wars.add(war);
            }
        }
        return wars;
    }
    
    public War getWarBetween(String country1, String country2) {
        for (War war : activeWars.values()) {
            if (war.isActive() && 
                ((war.getAttackerCountryId().equals(country1) && war.getDefenderCountryId().equals(country2)) ||
                 (war.getAttackerCountryId().equals(country2) && war.getDefenderCountryId().equals(country1)))) {
                return war;
            }
        }
        return null;
    }
    
    private void notifyAllPlayers(String message) {
        var server = CountryMod.getServer();
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.literal(message), false);
            }
        }
    }
}
