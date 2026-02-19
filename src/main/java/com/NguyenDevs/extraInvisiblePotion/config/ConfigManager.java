package com.NguyenDevs.extraInvisiblePotion.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.mergeDefaults();
    }

    private void mergeDefaults() {
        InputStream defaultStream = this.plugin.getResource("config.yml");
        if (defaultStream != null) {
            FileConfiguration defaults = YamlConfiguration
                    .loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            boolean changed = false;
            Iterator var4 = defaults.getKeys(true).iterator();

            while (var4.hasNext()) {
                String key = (String) var4.next();
                if (!this.config.contains(key)) {
                    this.config.set(key, defaults.get(key));
                    changed = true;
                }
            }

            if (changed) {
                try {
                    this.config.save(new File(this.plugin.getDataFolder(), "config.yml"));
                } catch (IOException var6) {
                    this.plugin.getLogger()
                            .warning("Could not save config.yml after merging defaults: " + var6.getMessage());
                }
            }

        }
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.mergeDefaults();
    }

    public Set<Material> getInvisiblePotionMaterials() {
        List<String> list = this.config.getStringList("invisible-potions");
        Set<Material> materials = new HashSet();
        Iterator var3 = list.iterator();

        while (var3.hasNext()) {
            String s = (String) var3.next();
            Material mat = Material.matchMaterial(s);
            if (mat != null) {
                materials.add(mat);
            }
        }

        return materials;
    }

    public Set<Material> getProjectileItemMaterials() {
        List<String> list = this.config.getStringList("projectile-items");
        Set<Material> materials = new HashSet();
        Iterator var3 = list.iterator();

        while (var3.hasNext()) {
            String s = (String) var3.next();
            Material mat = Material.matchMaterial(s);
            if (mat != null) {
                materials.add(mat);
            }
        }

        return materials;
    }

    public boolean isEnchantGlint() {
        return this.config.getBoolean("enchant-glint", true);
    }

    public String getInvisibleLore() {
        return this.config.getString("invisible-lore", "<#9B59B6>✦ Invisible</#9B59B6>");
    }

    public boolean isDurationLogicEnabled() {
        return this.config.getBoolean("duration-logic", true);
    }

    public String getExpirationLore() {
        return this.config.getString("expiration-lore", "&7Expire: &f%date%");
    }

    public String getDateFormat() {
        return this.config.getString("date-format", "HH:mm dd/MM/yyyy");
    }
}