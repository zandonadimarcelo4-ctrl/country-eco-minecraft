package com.countrymod.manager;

import com.countrymod.CountryMod;
import com.countrymod.model.*;
import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
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
    private final Gson gson;

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
            if (server == null) {
                CountryMod.LOGGER.error("Server instance is null. Cannot proceed with data operations.");
                return;
            }

            // ✅ Corrigido: getRunDirectory() já retorna Path
            String levelName = server.getSaveProperties().getLevelName();
            Path worldDir = server.getRunDirectory().resolve("saves").resolve(levelName);
            Path dataDir = worldDir.resolve(DATA_DIRECTORY);

            if (!Files.exists(dataDir)) {
                CountryMod.LOGGER.warn("Data directory does not exist: {}. Attempting to create it.", dataDir);
                Files.createDirectories(dataDir);
            }

            Path countriesFile = dataDir.resolve(COUNTRIES_FILE);
            CountryManager manager = CountryMod.getCountryManager();
            if (manager == null) {
                CountryMod.LOGGER.error("CountryManager instance is null. Cannot save data.");
                return;
            }

            Map<UUID, Country> countries = manager.getCountriesMap();
            if (countries == null) {
                CountryMod.LOGGER.error("Countries map is null. Cannot save data.");
                return;
            }

            String json = gson.toJson(countries);
            Files.writeString(countriesFile, json);
            CountryMod.LOGGER.info("Successfully saved {} countries to disk.", countries.size());

        } catch (IOException e) {
            CountryMod.LOGGER.error("IOException occurred while saving country data.", e);
        } catch (Exception e) {
            CountryMod.LOGGER.error("Unexpected exception occurred while saving country data.", e);
        }
    }

    /**
     * Load all country data from JSON
     */
    public void loadData(MinecraftServer server) {
        try {
            if (server == null) {
                CountryMod.LOGGER.error("Server instance is null. Cannot proceed with data operations.");
                return;
            }

            String levelName = server.getSaveProperties().getLevelName();
            Path worldDir = server.getRunDirectory().resolve("saves").resolve(levelName);
            Path dataDir = worldDir.resolve(DATA_DIRECTORY);

            if (!Files.exists(dataDir)) {
                CountryMod.LOGGER.warn("Data directory does not exist: {}. Creating it.", dataDir);
                Files.createDirectories(dataDir);
            }

            Path countriesFile = dataDir.resolve(COUNTRIES_FILE);
            if (!Files.exists(countriesFile)) {
                CountryMod.LOGGER.info("No country data file found at {}. Starting fresh.", countriesFile);
                return;
            }

            String json = Files.readString(countriesFile);
            JsonObject root;
            try {
                root = JsonParser.parseString(json).getAsJsonObject();
            } catch (JsonParseException e) {
                CountryMod.LOGGER.error("Failed to parse JSON from countries file. File might be corrupted: {}", countriesFile, e);
                return;
            }

            Map<UUID, Country> countries = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                try {
                    UUID countryId = UUID.fromString(entry.getKey());
                    Country country = gson.fromJson(entry.getValue(), Country.class);
                    countries.put(countryId, country);
                } catch (Exception e) {
                    CountryMod.LOGGER.error("Failed to parse country data for key: {}. Skipping entry.", entry.getKey(), e);
                }
            }

            CountryManager manager = CountryMod.getCountryManager();
            if (manager == null) {
                CountryMod.LOGGER.error("CountryManager instance is null. Cannot load data.");
                return;
            }

            manager.setCountriesMap(countries);
            CountryMod.LOGGER.info("Successfully loaded {} countries from disk.", countries.size());

        } catch (IOException e) {
            CountryMod.LOGGER.error("IOException occurred while loading country data.", e);
        } catch (Exception e) {
            CountryMod.LOGGER.error("Unexpected exception occurred while loading country data.", e);
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
