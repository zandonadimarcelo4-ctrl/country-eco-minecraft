package com.countrymod.item;

import com.countrymod.client.gui.LotteryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Lottery Ticket - Buy tickets and win the jackpot!
 */
public class LotteryTicketItem extends Item {
	public LotteryTicketItem(Settings settings) {
		super(settings.maxCount(64));
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient) {
			MinecraftClient client = MinecraftClient.getInstance();
			client.execute(() -> {
				client.setScreen(new LotteryScreen());
			});
		} else {
			user.sendMessage(Text.literal("§e§l[LOTTERY] §fFeeling lucky? Buy a ticket!"), false);
		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}
}
