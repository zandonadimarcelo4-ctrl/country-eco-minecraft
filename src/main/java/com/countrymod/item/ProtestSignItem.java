package com.countrymod.item;

import com.countrymod.client.gui.RevolutionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
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
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
                if (world.isClient()) {
                        handleClientSide();
                } else {
                        user.sendMessage(Text.literal("§c§l[REVOLUTION] §fPower to the people!"), false);
                }
                return ActionResult.SUCCESS;
        }

        private void handleClientSide() {
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                        client.setScreen(new RevolutionScreen());
                });
        }
}
