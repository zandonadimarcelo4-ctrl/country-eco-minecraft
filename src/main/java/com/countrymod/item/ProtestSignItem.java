package com.countrymod.item;

import com.countrymod.client.gui.RevolutionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Protest Sign - Start or join a revolution against corrupt leaders!
 */
public class ProtestSignItem extends Item {
        public ProtestSignItem(Settings settings) {
                super(settings.maxCount(1));
        }
        
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
                if (world.isClient()) {
                        handleClientSide();
                } else {
                        user.sendMessage(Text.literal("§c§l[REVOLUTION] §fPower to the people!"), false);
                }
                return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
        }

        private void handleClientSide() {
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                        client.setScreen(new RevolutionScreen());
                });
        }
}
