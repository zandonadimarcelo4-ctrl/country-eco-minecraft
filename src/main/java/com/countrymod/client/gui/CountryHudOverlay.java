package com.countrymod.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CountryHudOverlay {

    private final MinecraftClient client = MinecraftClient.getInstance();

    public void renderHud(DrawContext drawContext) {
        int x = 60;
        int y = 10;

        drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Country: [Name]"), x, y, 0xFFFFFF);
        drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Status: Citizen"), x, y + 10, 0x00FF00);
        drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Balance: $XXX"), x, y + 20, 0xFFD700);
    }
}
