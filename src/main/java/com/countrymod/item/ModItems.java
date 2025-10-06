package com.countrymod.item;

import com.countrymod.CountryMod;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Registers all custom items for the mod.
 */
public class ModItems {
	private static Item COUNTRY_FLAG;
	private static Item COLONY_FLAG;

	public static void register() {
		COUNTRY_FLAG = Items.WHITE_BANNER; // Usando um item existente
		COLONY_FLAG = Items.SHIELD; // Usando outro item existente
		
		// Não registrar itens existentes para evitar duplicação
		CountryMod.LOGGER.info("Using existing Minecraft items without re-registering");
	}

	@Environment(EnvType.CLIENT)
	public static void registerClientItems() {
		// Register client-side logic for items if needed
		// Example: Register screens or other client-specific behavior
	}
}
