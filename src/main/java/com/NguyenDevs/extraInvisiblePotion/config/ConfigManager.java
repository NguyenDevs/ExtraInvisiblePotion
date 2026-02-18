package com.NguyenDevs.extraInvisiblePotion.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private final Plugin plugin;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        mergeDefaults();
    }

    private void mergeDefaults() {
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream == null)
            return;
        FileConfiguration defaults = YamlConfiguration
                .loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
        boolean changed = false;
        for (String key : defaults.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaults.get(key));
                changed = true;
            }
        }
        if (changed) {
            try {
                config.save(new File(plugin.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save config.yml after merging defaults: " + e.getMessage());
            }
        }
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        mergeDefaults();
    }

    public Set<Material> getInvisiblePotionMaterials() {
        List<String> list = config.getStringList("invisible-potions");
        Set<Material> materials = new HashSet<>();
        for (String s : list) {
            Material mat = Material.matchMaterial(s);
            if (mat != null)
                materials.add(mat);
        }
        return materials;
    }

    public Set<Material> getProjectileItemMaterials() {
        List<String> list = config.getStringList("projectile-items");
        Set<Material> materials = new HashSet<>();
        for (String s : list) {
            Material mat = Material.matchMaterial(s);
            if (mat != null)
                materials.add(mat);
        }
        return materials;
    }

    public boolean isEnchantGlint() {
        return config.getBoolean("enchant-glint", true);
    }

    public String getInvisibleLore() {
        return config.getString("invisible-lore", "<#9B59B6>✦ Invisible</#9B59B6>");
    }
}
