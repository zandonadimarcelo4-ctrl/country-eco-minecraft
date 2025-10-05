package com.countrymod.manager;

import com.countrymod.CountryMod;
import com.countrymod.model.*;
import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Handles JSON-based persistence for country data.
 * Saves and loads country information to survive server restarts.
 */
public class DataPersistence {
	private static final String DATA_DIRECTORY = "countrymod_data";
	private static final String COUNTRIES_FILE = "countries.json";
	private Gson gson;
	
	public DataPersistence() {
		this.gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(BlockPos.class, new BlockPosSerializer())
			.registerTypeAdapter(BlockPos.class, new BlockPosDeserializer())
			.create();
	}
	
	/**
	 * Save all country data to JSON
	 */
	public void saveData(MinecraftServer server) {
		try {
			Path dataDir = server.getSavePath(null).resolve(DATA_DIRECTORY);
			Files.createDirectories(dataDir);
			
			Path countriesFile = dataDir.resolve(COUNTRIES_FILE);
			
			CountryManager manager = CountryMod.getCountryManager();
			Map<UUID, Country> countries = manager.getCountriesMap();
			
			// Convert to JSON
			String json = gson.toJson(countries);
			
			// Write to file
			Files.writeString(countriesFile, json);
			
			CountryMod.LOGGER.info("Saved {} countries to disk", countries.size());
		} catch (IOException e) {
			CountryMod.LOGGER.error("Failed to save country data", e);
		}
	}
	
	/**
	 * Load all country data from JSON
	 */
	public void loadData(MinecraftServer server) {
		try {
			Path dataDir = server.getSavePath(null).resolve(DATA_DIRECTORY);
			Path countriesFile = dataDir.resolve(COUNTRIES_FILE);
			
			if (!Files.exists(countriesFile)) {
				CountryMod.LOGGER.info("No country data file found, starting fresh");
				return;
			}
			
			// Read from file
			String json = Files.readString(countriesFile);
			
			// Parse JSON
			JsonObject root = JsonParser.parseString(json).getAsJsonObject();
			Map<UUID, Country> countries = new HashMap<>();
			
			for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
				UUID countryId = UUID.fromString(entry.getKey());
				Country country = gson.fromJson(entry.getValue(), Country.class);
				countries.put(countryId, country);
			}
			
			// Load into manager
			CountryManager manager = CountryMod.getCountryManager();
			manager.setCountriesMap(countries);
			
			CountryMod.LOGGER.info("Loaded {} countries from disk", countries.size());
		} catch (IOException e) {
			CountryMod.LOGGER.error("Failed to load country data", e);
		}
	}
	
	/**
	 * Custom serializer for BlockPos
	 */
	private static class BlockPosSerializer implements JsonSerializer<BlockPos> {
		@Override
		public JsonElement serialize(BlockPos src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty("x", src.getX());
			obj.addProperty("y", src.getY());
			obj.addProperty("z", src.getZ());
			return obj;
		}
	}
	
	/**
	 * Custom deserializer for BlockPos
	 */
	private static class BlockPosDeserializer implements JsonDeserializer<BlockPos> {
		@Override
		public BlockPos deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			int x = obj.get("x").getAsInt();
			int y = obj.get("y").getAsInt();
			int z = obj.get("z").getAsInt();
			return new BlockPos(x, y, z);
		}
	}
}
