package com.NguyenDevs.extraInvisiblePotion.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {

    private final Plugin plugin;
    private final File dataFile;
    private FileConfiguration data;
    private final Set<UUID> invisibleArmorPlayers = new HashSet<>();

    public PlayerDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
    }

    public void load() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create data.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        invisibleArmorPlayers.clear();
        for (String uuidStr : data.getStringList("invisible-armor-players")) {
            try {
                invisibleArmorPlayers.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        data.set("invisible-armor-players", invisibleArmorPlayers.stream().map(UUID::toString).toList());
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data.yml: " + e.getMessage());
        }
    }

    public void addInvisibleArmorPlayer(UUID uuid) {
        invisibleArmorPlayers.add(uuid);
    }

    public void removeInvisibleArmorPlayer(UUID uuid) {
        invisibleArmorPlayers.remove(uuid);
    }

    public boolean hasInvisibleArmorPlayer(UUID uuid) {
        return invisibleArmorPlayers.contains(uuid);
    }

    public Set<UUID> getInvisibleArmorPlayers() {
        return Set.copyOf(invisibleArmorPlayers);
    }
}
