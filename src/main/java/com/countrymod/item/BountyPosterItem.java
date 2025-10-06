package com.countrymod.item;

import com.countrymod.client.gui.BountyBoardScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Bounty Poster - Right click to see all active bounties and place new ones!
 */
public class BountyPosterItem extends Item {
        public BountyPosterItem(Settings settings) {
                super(settings.maxCount(1));
        }
        
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
                if (world.isClient()) {
                        handleClientSide();
                } else {
                        user.sendMessage(Text.literal("§6[BOUNTY BOARD] §fOpening wanted list..."), false);
                }
                return ActionResult.SUCCESS;
        }

        private void handleClientSide() {
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                        client.setScreen(new BountyBoardScreen());
                });
        }
}
