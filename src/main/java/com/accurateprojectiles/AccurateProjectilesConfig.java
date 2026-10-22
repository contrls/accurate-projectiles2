package com.accurateprojectiles;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The mod's settings, stored in config/accurate-projectiles.json.
 *
 * A missing, empty or broken file never crashes the game: whatever cannot be read simply
 * falls back to its default value and a warning is written to the log.
 */
public final class AccurateProjectilesConfig {
    private static final String FILE_NAME = "accurate-projectiles.json";

    private static final String KEY_PEARL = "removePearlSpread";
    private static final String KEY_WIND_CHARGE = "removeWindChargeSpread";
    private static final String KEY_DISPENSERS = "affectDispensers";
    private static final String KEY_BREEZE = "affectBreezeWindCharges";

    private static final boolean DEFAULT_PEARL = true;
    private static final boolean DEFAULT_WIND_CHARGE = true;
    private static final boolean DEFAULT_DISPENSERS = true;
    private static final boolean DEFAULT_BREEZE = false;

    private static volatile AccurateProjectilesConfig current = defaults();

    /** Remove the random spread of thrown ender pearls. */
    public final boolean removePearlSpread;
    /** Remove the random spread of player-thrown wind charges. */
    public final boolean removeWindChargeSpread;
    /** Also make dispenser-fired pearls and wind charges exact. */
    public final boolean affectDispensers;
    /** Also make wind charges shot by breezes exact. */
    public final boolean affectBreezeWindCharges;

    private AccurateProjectilesConfig(boolean removePearlSpread, boolean removeWindChargeSpread,
                                      boolean affectDispensers, boolean affectBreezeWindCharges) {
        this.removePearlSpread = removePearlSpread;
        this.removeWindChargeSpread = removeWindChargeSpread;
        this.affectDispensers = affectDispensers;
        this.affectBreezeWindCharges = affectBreezeWindCharges;
    }

    private static AccurateProjectilesConfig defaults() {
        return new AccurateProjectilesConfig(DEFAULT_PEARL, DEFAULT_WIND_CHARGE, DEFAULT_DISPENSERS, DEFAULT_BREEZE);
    }

    /** The settings currently in effect. Never null. */
    public static AccurateProjectilesConfig get() {
        return current;
    }

    /**
     * Reads the config file, creating it with the default values if it does not exist yet.
     * Never throws: on any problem the defaults (or the defaults for the broken options) are used.
     */
    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

        if (!Files.exists(path)) {
            current = defaults();
            writeDefaults(path);
            return;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root == null || !root.isJsonObject()) {
                AccurateProjectilesMod.LOGGER.warn("{} is empty or is not a JSON object; using the default settings.", path.getFileName());
                current = defaults();
                return;
            }

            JsonObject json = root.getAsJsonObject();
            current = new AccurateProjectilesConfig(
                readBoolean(json, KEY_PEARL, DEFAULT_PEARL),
                readBoolean(json, KEY_WIND_CHARGE, DEFAULT_WIND_CHARGE),
                readBoolean(json, KEY_DISPENSERS, DEFAULT_DISPENSERS),
                readBoolean(json, KEY_BREEZE, DEFAULT_BREEZE)
            );
        } catch (IOException | RuntimeException e) {
            AccurateProjectilesMod.LOGGER.warn("Could not read {}; using the default settings.", path.getFileName(), e);
            current = defaults();
        }
    }

    private static boolean readBoolean(JsonObject json, String key, boolean fallback) {
        JsonElement element = json.get(key);
        if (element == null) {
            return fallback;
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        }
        AccurateProjectilesMod.LOGGER.warn("Option \"{}\" must be true or false; using the default ({}).", key, fallback);
        return fallback;
    }

    private static void writeDefaults(Path path) {
        JsonObject json = new JsonObject();
        json.addProperty(KEY_PEARL, DEFAULT_PEARL);
        json.addProperty(KEY_WIND_CHARGE, DEFAULT_WIND_CHARGE);
        json.addProperty(KEY_DISPENSERS, DEFAULT_DISPENSERS);
        json.addProperty(KEY_BREEZE, DEFAULT_BREEZE);

        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
            }
        } catch (IOException | RuntimeException e) {
            AccurateProjectilesMod.LOGGER.warn("Could not write the default config file {}.", path.getFileName(), e);
        }
    }
}
