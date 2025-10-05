package com.countrymod.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LotteryScreen extends Screen {
    public LotteryScreen() {
        super(Text.literal("Lottery"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, 0, 0, 0);
        super.render(context, mouseX, mouseY, delta);
    }
}
