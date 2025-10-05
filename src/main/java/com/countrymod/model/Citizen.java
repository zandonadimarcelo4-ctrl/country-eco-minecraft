package com.countrymod.model;

import java.util.UUID;

/**
 * Represents a citizen or member of a country.
 * Tracks citizenship level, military status, permissions, and CPF (citizen identifier).
 * CPF is used as the primary document identifier for all official country business.
 */
public class Citizen {
        private UUID playerUuid;
        private String playerName;
        private String cpf; // Brazilian CPF - primary citizen identifier
        private CitizenshipLevel citizenshipLevel;
        private MilitaryRank militaryRank; // null if not in military
        private long joinDate; // Timestamp when they joined
        private boolean isLeader;
        private boolean isHeir; // For monarchies
        private int contributionPoints; // For reputation/progression system
        
        public Citizen(UUID playerUuid, String playerName, CitizenshipLevel level) {
                this.playerUuid = playerUuid;
                this.playerName = playerName;
                this.cpf = null; // Will be set from economy account
                this.citizenshipLevel = level;
                this.militaryRank = null;
                this.joinDate = System.currentTimeMillis();
                this.isLeader = false;
                this.isHeir = false;
                this.contributionPoints = 0;
        }
        
        // Getters and setters
        public UUID getPlayerUuid() {
                return playerUuid;
        }
        
        public String getPlayerName() {
                return playerName;
        }
        
        public void setPlayerName(String playerName) {
                this.playerName = playerName;
        }
        
        public String getCpf() {
                return cpf;
        }
        
        public void setCpf(String cpf) {
                this.cpf = cpf;
        }
        
        public boolean hasCpf() {
                return cpf != null && !cpf.isEmpty();
        }
        
        public CitizenshipLevel getCitizenshipLevel() {
                return citizenshipLevel;
        }
        
        public void setCitizenshipLevel(CitizenshipLevel citizenshipLevel) {
                this.citizenshipLevel = citizenshipLevel;
        }
        
        public MilitaryRank getMilitaryRank() {
                return militaryRank;
        }
        
        public void setMilitaryRank(MilitaryRank militaryRank) {
                this.militaryRank = militaryRank;
        }
        
        public boolean isInMilitary() {
                return militaryRank != null;
        }
        
        public long getJoinDate() {
                return joinDate;
        }
        
        public boolean isLeader() {
                return isLeader;
        }
        
        public void setLeader(boolean leader) {
                isLeader = leader;
        }
        
        public boolean isHeir() {
                return isHeir;
        }
        
        public void setHeir(boolean heir) {
                isHeir = heir;
        }
        
        public int getContributionPoints() {
                return contributionPoints;
        }
        
        public void addContributionPoints(int points) {
                this.contributionPoints += points;
        }
        
        /**
         * Check if this citizen has permission to perform an action based on their level
         */
        public boolean hasPermission(String action) {
                switch (action) {
                        case "build":
                                return citizenshipLevel.canBuild();
                        case "vote":
                                return citizenshipLevel.canVote();
                        case "military":
                                return citizenshipLevel.canJoinMilitary();
                        default:
                                return false;
                }
        }
}
