package com.countrymod.item;
import com.countrymod.client.gui.CountryCreationScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Special item used to create a country.
 * Right-clicking on the ground opens the country creation GUI.
 */
public class CountryFlagItem extends Item {
	public CountryFlagItem(Settings settings) {
		super(settings.maxCount(1));
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		
		if (world.isClient) {
			// Open country creation GUI on client
			MinecraftClient client = MinecraftClient.getInstance();
			client.execute(() -> {
				client.setScreen(new CountryCreationScreen(context.getBlockPos().up()));
			});
		}
		
		return ActionResult.SUCCESS;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient) {
			user.sendMessage(Text.literal("Place the flag on the ground to create your country!"), false);
		}
		return TypedActionResult.pass(user.getStackInHand(hand));
	}
}
