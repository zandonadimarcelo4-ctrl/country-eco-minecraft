package com.countrymod.item;

import com.countrymod.CountryMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registers all custom items for the mod.
 */
public class ModItems {
	public static final Item COUNTRY_FLAG = new CountryFlagItem(new Item.Settings());
	public static final Item COLONY_FLAG = new ColonyFlagItem(new Item.Settings());
	
	public static void register() {
		Registry.register(Registries.ITEM, Identifier.of(CountryMod.MOD_ID, "country_flag"), COUNTRY_FLAG);
		Registry.register(Registries.ITEM, Identifier.of(CountryMod.MOD_ID, "colony_flag"), COLONY_FLAG);
		
		CountryMod.LOGGER.info("Registered mod items");
	}
}
