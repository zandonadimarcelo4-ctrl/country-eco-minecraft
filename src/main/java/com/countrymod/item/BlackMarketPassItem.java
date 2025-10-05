package com.countrymod.item;

import com.countrymod.client.gui.BlackMarketScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

/**
 * Black Market Pass - Access the underground economy!
 * Buy and sell illegal items for profit (but you might get caught!)
 */
public class BlackMarketPassItem extends Item {
	public BlackMarketPassItem(Settings settings) {
		super(settings.maxCount(1));
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient()) {
			MinecraftClient client = MinecraftClient.getInstance();
			client.execute(() -> {
				client.setScreen(new BlackMarketScreen());
			});
		} else {
			user.sendMessage(Text.literal("§8§l[BLACK MARKET] §7Psst... looking for something special?"), false);
		}
		return ActionResult.SUCCESS);
	}
}
