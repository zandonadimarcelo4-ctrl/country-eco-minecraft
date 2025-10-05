package com.countrymod;

import com.countrymod.client.gui.CountryHudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("countrymod-client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Country System Mod Client");
		
		// Register HUD overlay for country information
		HudRenderCallback.EVENT.register(new CountryHudOverlay());
		
		// Register particle effects
		// Note: Particle registration would go here
		
		LOGGER.info("Country System Mod Client initialized successfully");
	}
}
