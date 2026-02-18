package com.NguyenDevs.extraInvisiblePotion;

import com.NguyenDevs.extraInvisiblePotion.armor.ArmorEquipListener;
import com.NguyenDevs.extraInvisiblePotion.armor.ArmorInvisibilityApplier;
import com.NguyenDevs.extraInvisiblePotion.command.EIPTabCompleter;
import com.NguyenDevs.extraInvisiblePotion.command.ReloadCommand;
import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.data.PlayerDataManager;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGui;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGuiListener;
import com.NguyenDevs.extraInvisiblePotion.item.ItemInvisibilityListener;
import com.NguyenDevs.extraInvisiblePotion.projectile.ProjectileInvisibilityListener;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExtraInvisiblePotion extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        ItemDataUtil.init(this);

        configManager = new ConfigManager(this);
        configManager.load();

        messageManager = new MessageManager(this);
        messageManager.load();

        playerDataManager = new PlayerDataManager(this);
        playerDataManager.load();

        ArmorInvisibilityApplier armorApplier = new ArmorInvisibilityApplier(playerDataManager);
        InvisibleCraftingGui craftingGui = new InvisibleCraftingGui(configManager);

        getServer().getPluginManager().registerEvents(new InvisibleCraftingGuiListener(craftingGui, messageManager),
                this);
        getServer().getPluginManager().registerEvents(new ArmorEquipListener(armorApplier), this);
        getServer().getPluginManager().registerEvents(new ItemInvisibilityListener(), this);
        getServer().getPluginManager().registerEvents(new ProjectileInvisibilityListener(configManager), this);

        ReloadCommand reloadCommand = new ReloadCommand(configManager, messageManager, craftingGui);
        EIPTabCompleter tabCompleter = new EIPTabCompleter();

        var cmd = getCommand("eip");
        if (cmd != null) {
            cmd.setExecutor(reloadCommand);
            cmd.setTabCompleter(tabCompleter);
        }

        getLogger().info("ExtraInvisiblePotion enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
        getLogger().info("ExtraInvisiblePotion disabled.");
    }
}
