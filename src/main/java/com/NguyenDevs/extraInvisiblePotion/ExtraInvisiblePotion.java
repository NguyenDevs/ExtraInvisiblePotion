package com.NguyenDevs.extraInvisiblePotion;

import com.NguyenDevs.extraInvisiblePotion.command.EIPTabCompleter;
import com.NguyenDevs.extraInvisiblePotion.command.ReloadCommand;
import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.data.PlayerDataManager;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGui;
import com.NguyenDevs.extraInvisiblePotion.listener.InvisibleCraftingGuiListener;
import com.NguyenDevs.extraInvisiblePotion.packet.EquipmentPacketListener;
import com.NguyenDevs.extraInvisiblePotion.listener.ProjectileInvisibilityListener;
import com.NguyenDevs.extraInvisiblePotion.listener.AnvilInvisibleListener;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExtraInvisiblePotion extends JavaPlugin {
        private ConfigManager configManager;
        private MessageManager messageManager;
        private PlayerDataManager playerDataManager;

        public void onEnable() {
                this.printLogoEIP();
                ItemDataUtil.init(this);
                this.configManager = new ConfigManager(this);
                this.configManager.load();
                this.messageManager = new MessageManager(this);
                this.messageManager.load();
                this.playerDataManager = new PlayerDataManager(this);
                this.playerDataManager.load();
                InvisibleCraftingGui craftingGui = new InvisibleCraftingGui(this.configManager);
                this.getServer().getPluginManager().registerEvents(
                                new InvisibleCraftingGuiListener(craftingGui, this.messageManager), this);
                this.getServer().getPluginManager()
                                .registerEvents(new ProjectileInvisibilityListener(this.configManager), this);
                this.getServer().getPluginManager().registerEvents(
                                new AnvilInvisibleListener(this.configManager, this.messageManager, craftingGui), this);
                ProtocolLibrary.getProtocolManager().addPacketListener(new EquipmentPacketListener(this));

                new com.NguyenDevs.extraInvisiblePotion.task.InvisibleExpirationTask(this.configManager)
                                .runTaskTimer(this, 20L, 20L);

                ReloadCommand reloadCommand = new ReloadCommand(this.configManager, this.messageManager, craftingGui);
                EIPTabCompleter tabCompleter = new EIPTabCompleter();
                PluginCommand cmd = this.getCommand("eip");
                if (cmd != null) {
                        cmd.setExecutor(reloadCommand);
                        cmd.setTabCompleter(tabCompleter);
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&7[&fEIP&7] &aExtraInvisiblePotion plugin enabled successfully."));
        }

        public void onDisable() {
                if (this.playerDataManager != null) {
                        this.playerDataManager.save();
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&7[&fEIP&7] &cExtraInvisiblePotion plugin disabled."));
        }

        public void printLogoEIP() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&f ███████╗██╗██████╗ "));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&f ██╔════╝██║██╔══██╗"));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7 █████╗  ██║██████╔╝"));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7 ██╔══╝  ██║██╔═══╝ "));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8 ███████╗██║██║     "));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8 ╚══════╝╚═╝╚═╝    "));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                Bukkit.getConsoleSender()
                                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&f Extra Invisible Potion"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&6 Version " + this.getDescription().getVersion()));
                Bukkit.getConsoleSender().sendMessage(
                                ChatColor.translateAlternateColorCodes('&', "&b Development by NguyenDevs"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        }
}