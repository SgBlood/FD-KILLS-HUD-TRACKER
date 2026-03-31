package dev.aymeric.armorfdtracker.config;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir().resolve("armorfdtracker.json");
    private static final Gson GSON = new Gson();

    public int hudX = 5;
    public int hudY = 5;
    public float hudScale = 1.0f;

    private static ModConfig instance;

    public static ModConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    private static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                return GSON.fromJson(Files.readString(CONFIG_PATH), ModConfig.class);
            } catch (Exception e) {
                return new ModConfig();
            }
        }
        return new ModConfig();
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            // ignore
        }
    }
}
