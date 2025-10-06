package com.countrymod.system;

import com.countrymod.CountryMod;
import com.countrymod.model.Country;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Diplomatic alliance system between countries
 */
public class AllianceSystem {
    private final Map<String, Alliance> alliances = new HashMap<>();
    private final Map<String, Set<String>> countryAlliances = new HashMap<>();
    
    public static class Alliance {
        private final String allianceId;
        private final String name;
        private final Set<String> memberCountries;
        private final String founderCountryId;
        private final long createdAt;
        private double sharedTreasury;
        
        public Alliance(String name, String founderCountryId) {
            this.allianceId = UUID.randomUUID().toString();
            this.name = name;
            this.founderCountryId = founderCountryId;
            this.memberCountries = new HashSet<>();
            this.memberCountries.add(founderCountryId);
            this.createdAt = System.currentTimeMillis();
            this.sharedTreasury = 0.0;
        }
        
        public String getAllianceId() { return allianceId; }
        public String getName() { return name; }
        public Set<String> getMemberCountries() { return new HashSet<>(memberCountries); }
        public String getFounderCountryId() { return founderCountryId; }
        public long getCreatedAt() { return createdAt; }
        public double getSharedTreasury() { return sharedTreasury; }
        
        public void addMember(String countryId) {
            memberCountries.add(countryId);
        }
        
        public void removeMember(String countryId) {
            memberCountries.remove(countryId);
        }
        
        public void depositToTreasury(double amount) {
            sharedTreasury += amount;
        }
        
        public boolean withdrawFromTreasury(double amount) {
            if (sharedTreasury >= amount) {
                sharedTreasury -= amount;
                return true;
            }
            return false;
        }
        
        public boolean isMember(String countryId) {
            return memberCountries.contains(countryId);
        }
    }
    
    public Alliance createAlliance(String name, Country founder) {
        Alliance alliance = new Alliance(name, founder.getCountryId().toString());
        alliances.put(alliance.getAllianceId(), alliance);
        
        countryAlliances.computeIfAbsent(founder.getCountryId().toString(), k -> new HashSet<>())
            .add(alliance.getAllianceId());
        
        notifyAllPlayers("§b§l[ALLIANCE] §f" + founder.getName() + " §bhas formed the alliance: §f" + name);
        
        CountryMod.LOGGER.info("Alliance '{}' created by {}", name, founder.getName());
        return alliance;
    }
    
    public boolean inviteToAlliance(Alliance alliance, Country invitingCountry) {
        if (alliance.isMember(invitingCountry.getCountryId().toString())) {
            return false;
        }
        
        alliance.addMember(invitingCountry.getCountryId().toString());
        countryAlliances.computeIfAbsent(invitingCountry.getCountryId().toString(), k -> new HashSet<>())
            .add(alliance.getAllianceId());
        
        notifyAllPlayers("§b[ALLIANCE] §f" + invitingCountry.getName() + " §bhas joined §f" + alliance.getName());
        
        return true;
    }
    
    public void leaveAlliance(String countryId, String allianceId) {
        Alliance alliance = alliances.get(allianceId);
        if (alliance == null) return;
        
        alliance.removeMember(countryId);
        
        Set<String> countryAlls = countryAlliances.get(countryId);
        if (countryAlls != null) {
            countryAlls.remove(allianceId);
        }
        
        if (alliance.getMemberCountries().isEmpty()) {
            alliances.remove(allianceId);
            CountryMod.LOGGER.info("Alliance '{}' disbanded (no members)", alliance.getName());
        }
    }
    
    public boolean areAllied(String country1, String country2) {
        Set<String> alliances1 = countryAlliances.get(country1);
        Set<String> alliances2 = countryAlliances.get(country2);
        
        if (alliances1 == null || alliances2 == null) {
            return false;
        }
        
        for (String allianceId : alliances1) {
            if (alliances2.contains(allianceId)) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<Alliance> getAllAlliances() {
        return new ArrayList<>(alliances.values());
    }
    
    public Alliance getAlliance(String allianceId) {
        return alliances.get(allianceId);
    }
    
    public Set<Alliance> getCountryAlliances(String countryId) {
        Set<Alliance> result = new HashSet<>();
        Set<String> allianceIds = countryAlliances.get(countryId);
        
        if (allianceIds != null) {
            for (String allianceId : allianceIds) {
                Alliance alliance = alliances.get(allianceId);
                if (alliance != null) {
                    result.add(alliance);
                }
            }
        }
        
        return result;
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
