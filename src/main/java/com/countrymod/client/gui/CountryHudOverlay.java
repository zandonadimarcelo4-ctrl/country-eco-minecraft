package com.countrymod.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

/**
 * HUD overlay displaying country information and status.
 * Shows current country, citizenship level, balance, and alerts.
 */
public class CountryHudOverlay implements HudRenderCallback {
	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden) {
			return;
		}
		
	int x = 10;
	int y = 10;

	// TODO: Get actual country data from client-side state
	// For now, this is a placeholder showing the HUD structure
	// Draw simple placeholders so x/y are used and warnings suppressed
	drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Country: [Name]"), x + 50, y, 0xFFFFFF);
	drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Status: Citizen"), x + 50, y + 10, 0x00FF00);
	drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Balance: $XXX"), x + 50, y + 20, 0xFFD700);
	}
}
