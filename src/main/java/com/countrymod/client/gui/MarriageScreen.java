package com.countrymod.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MarriageScreen extends Screen {
    public MarriageScreen() {
        super(Text.literal("Marriage"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, 0, 0, 0);
        super.render(context, mouseX, mouseY, delta);
    }
}
