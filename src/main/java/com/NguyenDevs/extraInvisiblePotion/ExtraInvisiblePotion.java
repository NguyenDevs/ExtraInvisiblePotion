package com.NguyenDevs.extraInvisiblePotion;

import com.NguyenDevs.extraInvisiblePotion.packet.EquipmentPacketListener;
import com.NguyenDevs.extraInvisiblePotion.projectile.ProjectileInvisibilityListener;
import com.comphenix.protocol.ProtocolLibrary;

import com.NguyenDevs.extraInvisiblePotion.command.EIPTabCompleter;
import com.NguyenDevs.extraInvisiblePotion.command.ReloadCommand;
import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.data.PlayerDataManager;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGui;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGuiListener;

import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExtraInvisiblePotion extends JavaPlugin {

        private ConfigManager configManager;
        private MessageManager messageManager;
        private PlayerDataManager playerDataManager;

        @Override
        public void onEnable() {
                printLogoEIP();

                ItemDataUtil.init(this);

                configManager = new ConfigManager(this);
                configManager.load();

                messageManager = new MessageManager(this);
                messageManager.load();

                playerDataManager = new PlayerDataManager(this);
                playerDataManager.load();

                InvisibleCraftingGui craftingGui = new InvisibleCraftingGui(configManager);

                getServer().getPluginManager().registerEvents(
                                new InvisibleCraftingGuiListener(craftingGui, messageManager),
                                this);
                // getServer().getPluginManager().registerEvents(new
                // ArmorEquipListener(armorApplier), this); // Removed
                // getServer().getPluginManager().registerEvents(new ItemInvisibilityListener(),
                // this); // Removed
                getServer().getPluginManager().registerEvents(new ProjectileInvisibilityListener(configManager), this);

                ProtocolLibrary.getProtocolManager()
                                .addPacketListener(new EquipmentPacketListener(this, configManager));

                ReloadCommand reloadCommand = new ReloadCommand(configManager, messageManager, craftingGui);
                EIPTabCompleter tabCompleter = new EIPTabCompleter();

                var cmd = getCommand("eip");
                if (cmd != null) {
                        cmd.setExecutor(reloadCommand);
                        cmd.setTabCompleter(tabCompleter);
                }

                org.bukkit.Bukkit.getConsoleSender().sendMessage(
                                org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&7[&fEIP&7] &aExtraInvisiblePotion plugin enabled successfully."));
        }

        @Override
        public void onDisable() {
                if (playerDataManager != null) {
                        playerDataManager.save();
                }
                org.bukkit.Bukkit.getConsoleSender().sendMessage(
                                org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&7[&fEIP&7] &cExtraInvisiblePotion plugin disabled."));
        }

        public void printLogoEIP() {
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', ""));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&f ███████╗██╗██████╗ "));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&f ██╔════╝██║██╔══██╗"));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&7 █████╗  ██║██████╔╝"));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&7 ██╔══╝  ██║██╔═══╝ "));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&8 ███████╗██║██║     "));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&8 ╚══════╝╚═╝╚═╝    "));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', ""));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&f Extra Invisible Potion"));
                org.bukkit.Bukkit.getConsoleSender().sendMessage(
                                org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&6 Version " + getDescription().getVersion()));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                                                "&b Development by NguyenDevs"));
                org.bukkit.Bukkit.getConsoleSender()
                                .sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', ""));
        }

}
