package com.countrymod.system;

import com.countrymod.CountryMod;
import com.countrymod.model.Country;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages tax collection for countries.
 * Leaders can set tax rates and collect taxes from citizens.
 */
public class TaxSystem {
    private final Map<String, Double> taxRates = new HashMap<>();
    private final Map<String, Long> lastTaxCollection = new HashMap<>();
    private static final long TAX_COLLECTION_INTERVAL = 20 * 60 * 60; // 1 hour in ticks
    
    public void setTaxRate(String countryId, double rate) {
        if (rate < 0 || rate > 50) {
            throw new IllegalArgumentException("Tax rate must be between 0% and 50%");
        }
        taxRates.put(countryId, rate);
        CountryMod.LOGGER.info("Tax rate for country {} set to {}%", countryId, rate);
    }
    
    public double getTaxRate(String countryId) {
        return taxRates.getOrDefault(countryId, 0.0);
    }
    
    public boolean canCollectTax(String countryId) {
        long lastCollection = lastTaxCollection.getOrDefault(countryId, 0L);
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCollection) >= TAX_COLLECTION_INTERVAL;
    }
    
    public double collectTaxes(Country country) {
        double totalCollected = 0.0;
        double taxRate = getTaxRate(country.getCountryId().toString());
        
        if (taxRate == 0) {
            return 0.0;
        }
        
        var economyManager = CountryMod.getEconomyManager();
        var server = CountryMod.getServer();
        
        if (server == null) {
            return 0.0;
        }
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (country.getCitizen(player.getUuid()) != null) {
                var account = economyManager.getAccount(player.getUuid());
                if (account != null && account.getBalance() > 0) {
                    double taxAmount = account.getBalance() * (taxRate / 100.0);
                    if (account.withdraw(taxAmount)) {
                        totalCollected += taxAmount;
                    }
                    
                    player.sendMessage(Text.literal("§c[TAX] §fYou paid REI$ " + 
                        String.format("%.2f", taxAmount) + " in taxes (" + taxRate + "%)"), false);
                }
            }
        }
        
        lastTaxCollection.put(country.getCountryId().toString(), System.currentTimeMillis());
        country.addToTreasury(totalCollected);
        
        return totalCollected;
    }
    
    public void distributeTreasury(Country country, double amount) {
        if (country.getTreasuryBalance() < amount) {
            return;
        }
        
        country.withdrawFromTreasury(amount);
        var server = CountryMod.getServer();
        
        if (server == null) {
            return;
        }
        
        int citizenCount = 0;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (country.getCitizen(player.getUuid()) != null) {
                citizenCount++;
            }
        }
        
        if (citizenCount == 0) {
            return;
        }
        
        double amountPerCitizen = amount / citizenCount;
        var economyManager = CountryMod.getEconomyManager();
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (country.getCitizen(player.getUuid()) != null) {
                var account = economyManager.getAccount(player.getUuid());
                if (account != null) {
                    account.deposit(amountPerCitizen, "Treasury Distribution");
                    player.sendMessage(Text.literal("§a[TREASURY] §fYou received REI$ " + 
                        String.format("%.2f", amountPerCitizen) + " from the national treasury!"), false);
                }
            }
        }
    }
}
