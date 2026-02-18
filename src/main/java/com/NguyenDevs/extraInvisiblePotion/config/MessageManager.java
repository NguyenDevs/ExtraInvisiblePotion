package com.NguyenDevs.extraInvisiblePotion.config;

import com.NguyenDevs.extraInvisiblePotion.util.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final Plugin plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessageManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        mergeDefaults();
    }

    private void mergeDefaults() {
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream == null)
            return;
        FileConfiguration defaults = YamlConfiguration
                .loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
        boolean changed = false;
        for (String key : defaults.getKeys(true)) {
            if (!messages.contains(key)) {
                messages.set(key, defaults.get(key));
                changed = true;
            }
        }
        if (changed) {
            try {
                messages.save(messagesFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save messages.yml after merging defaults: " + e.getMessage());
            }
        }
    }

    public void reload() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        mergeDefaults();
    }

    public String getMessage(String key) {
        String prefix = ColorUtil.colorize(messages.getString("prefix", ""));
        String raw = messages.getString(key, "&cMessage not found: " + key);
        raw = raw.replace("{prefix}", prefix);
        return ColorUtil.colorize(raw);
    }
}
