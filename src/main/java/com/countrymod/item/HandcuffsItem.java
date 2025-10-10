package com.countrymod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Handcuffs - Use on another player to arrest them (if you have authority)!
 */
public class HandcuffsItem extends Item {
        public HandcuffsItem(Settings settings) {
                super(settings.maxCount(16));
        }
        
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
                if (!world.isClient()) {
                        user.sendMessage(Text.literal("§c[POLICE] §fRight-click on a player to attempt arrest!"), false);
                }
                return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
        }
}
