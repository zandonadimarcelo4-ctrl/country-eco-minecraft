package com.countrymod.item;

import com.countrymod.client.gui.ColonyCreationScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Special item used to create a colony.
 * Right-clicking on the ground opens the colony creation GUI (client-side only).
 */
public class ColonyFlagItem extends Item {
    public ColonyFlagItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        if (world.isClient()) {
            openScreen(context);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            user.sendMessage(Text.literal("Place the flag on the ground to create a colony!"), false);
        }
        return ActionResult.PASS;
    }

    /**
     * Opens the colony creation screen (client-only).
     */
    @Environment(EnvType.CLIENT)
    private void openScreen(ItemUsageContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            client.setScreen(new ColonyCreationScreen(context.getBlockPos().up()));
        });
    }
}