package com.countrymod.manager;

import com.countrymod.model.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import java.util.*;

/**
 * Central manager for all country-related operations.
 * Handles country creation, citizen management, and territory control.
 */
public class CountryManager {
        private Map<UUID, Country> countries; // countryId -> Country
        private Map<UUID, UUID> playerCountries; // playerUuid -> countryId
        private Map<BlockPos, UUID> flagPositions; // flagPos -> countryId
        
        public CountryManager() {
                this.countries = new HashMap<>();
                this.playerCountries = new HashMap<>();
                this.flagPositions = new HashMap<>();
        }
        
        /**
         * Create a new country
         */
        public Country createCountry(String name, GovernmentType governmentType, BlockPos flagPos, UUID leaderId, String leaderName, String cpf) {
                Country country = new Country(name, governmentType, flagPos, leaderId);
                
                // Add leader as a citizen with CPF
                Citizen leader = new Citizen(leaderId, leaderName, CitizenshipLevel.CITIZEN);
                leader.setLeader(true);
                leader.setCpf(cpf); // Set CPF as citizen identifier
                country.addCitizen(leader);
                
                // Register country
                countries.put(country.getCountryId(), country);
                playerCountries.put(leaderId, country.getCountryId());
                flagPositions.put(flagPos, country.getCountryId());
                
                return country;
        }
        
        /**
         * Get country by ID
         */
        public Country getCountry(UUID countryId) {
                return countries.get(countryId);
        }
        
        /**
         * Get country by player UUID
         */
        public Country getCountryByPlayer(UUID playerUuid) {
                UUID countryId = playerCountries.get(playerUuid);
                return countryId != null ? countries.get(countryId) : null;
        }
        
        /**
         * Get country by flag position
         */
        public Country getCountryByFlag(BlockPos flagPos) {
                UUID countryId = flagPositions.get(flagPos);
                return countryId != null ? countries.get(countryId) : null;
        }
        
        /**
         * Get country at a specific position (check territory)
         */
        public Country getCountryAtPosition(BlockPos pos) {
                for (Country country : countries.values()) {
                        if (country.getTerritory().isPositionInTerritory(pos)) {
                                return country;
                        }
                }
                return null;
        }
        
        /**
         * Add a citizen to a country with CPF identifier
         */
        public void addCitizenToCountry(UUID countryId, UUID playerUuid, String playerName, CitizenshipLevel level, String cpf) {
                Country country = countries.get(countryId);
                if (country != null) {
                        Citizen citizen = new Citizen(playerUuid, playerName, level);
                        citizen.setCpf(cpf); // Set CPF as primary identifier
                        country.addCitizen(citizen);
                        playerCountries.put(playerUuid, countryId);
                }
        }
        
        /**
         * Remove a citizen from a country
         */
        public void removeCitizenFromCountry(UUID countryId, UUID playerUuid) {
                Country country = countries.get(countryId);
                if (country != null) {
                        country.removeCitizen(playerUuid);
                        playerCountries.remove(playerUuid);
                }
        }
        
        /**
         * Handle leader death - start takeover window
         */
        public void handleLeaderDeath(UUID countryId, UUID killerId) {
                Country country = countries.get(countryId);
                if (country != null && country.canBeAttacked()) {
                        country.startTakeover(killerId);
                }
        }
        
        /**
         * Attempt flag capture for takeover
         */
        public boolean attemptFlagCapture(BlockPos flagPos, UUID playerUuid) {
                Country country = getCountryByFlag(flagPos);
                if (country != null && country.isUnderAttack() && 
                    country.getAttackerId().equals(playerUuid) && 
                    country.isTakeoverWindowActive()) {
                        country.completeTakeover(playerUuid);
                        
                        // Add attacker as citizen if not already
                        if (country.getCitizen(playerUuid) == null) {
                                addCitizenToCountry(country.getCountryId(), playerUuid, "", CitizenshipLevel.CITIZEN, "");
                        }
                        
                        return true;
                }
                return false;
        }
        
        /**
         * Create a colony for a country
         */
        public Colony createColony(UUID countryId, String colonyName, BlockPos flagPos, UUID governorId, ColonyType type) {
                Country country = countries.get(countryId);
                if (country != null) {
                        Colony colony = new Colony(colonyName, countryId, flagPos, governorId, type);
                        country.addColony(colony);
                        flagPositions.put(flagPos, countryId); // Associate flag with parent country
                        return colony;
                }
                return null;
        }
        
        /**
         * Handle colony independence - convert to new country
         */
        public Country grantColonyIndependence(Colony colony, GovernmentType newGovernmentType) {
                Country parentCountry = countries.get(colony.getParentCountryId());
                if (parentCountry != null) {
                        // Remove colony from parent
                        parentCountry.removeColony(colony);
                        
                        // Create new country from colony
                        Country newCountry = new Country(colony.getName(), newGovernmentType, 
                                                          colony.getFlagPosition(), colony.getGovernorId());
                        newCountry.getTerritory().getClaimedChunks().addAll(colony.getTerritory().getClaimedChunks());
                        
                        // Register new country
                        countries.put(newCountry.getCountryId(), newCountry);
                        flagPositions.put(colony.getFlagPosition(), newCountry.getCountryId());
                        
                        return newCountry;
                }
                return null;
        }
        
        /**
         * Get all countries
         */
        public Collection<Country> getAllCountries() {
                return countries.values();
        }
        
        /**
         * Delete a country
         */
        public void deleteCountry(UUID countryId) {
                Country country = countries.get(countryId);
                if (country != null) {
                        // Remove all player associations
                        for (UUID playerUuid : country.getCitizens().keySet()) {
                                playerCountries.remove(playerUuid);
                        }
                        
                        // Remove flag position
                        flagPositions.remove(country.getFlagPosition());
                        
                        // Remove country
                        countries.remove(countryId);
                }
        }
        
        // Data access for persistence
        public Map<UUID, Country> getCountriesMap() {
                return countries;
        }
        
        public void setCountriesMap(Map<UUID, Country> countries) {
                this.countries = countries;
                
                // Rebuild indices
                playerCountries.clear();
                flagPositions.clear();
                
                for (Country country : countries.values()) {
                        flagPositions.put(country.getFlagPosition(), country.getCountryId());
                        for (UUID playerUuid : country.getCitizens().keySet()) {
                                playerCountries.put(playerUuid, country.getCountryId());
                        }
                }
        }
}
