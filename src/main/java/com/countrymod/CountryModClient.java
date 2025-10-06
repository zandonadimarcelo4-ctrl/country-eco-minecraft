package com.countrymod;

import com.countrymod.client.gui.CountryHudOverlay;
import com.countrymod.item.BlackMarketPassItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("countrymod-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Country System Mod Client");

        CountryHudOverlay overlay = new CountryHudOverlay();

        // Registro correto do HUD overlay usando lambda
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> overlay.renderHud(drawContext));

        // Register client-side logic for BlackMarketPassItem
        BlackMarketPassItem.registerClientLogic();

        LOGGER.info("Country System Mod Client initialized successfully");
    }
}
