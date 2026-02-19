package com.NguyenDevs.extraInvisiblePotion.task;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;

import com.NguyenDevs.extraInvisiblePotion.util.ColorUtil;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

public class InvisibleExpirationTask extends BukkitRunnable {

    private final ConfigManager configManager;

    public InvisibleExpirationTask(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void run() {
        if (!configManager.isDurationLogicEnabled())
            return;

        long now = System.currentTimeMillis();

        for (Player player : Bukkit.getOnlinePlayers()) {
            // Check armor
            ItemStack[] armor = player.getInventory().getArmorContents();
            boolean changed = false;
            for (int i = 0; i < armor.length; i++) {
                if (checkAndExpire(armor[i], now)) {
                    changed = true;
                }
            }
            if (changed) {
                player.getInventory().setArmorContents(armor);
            }

            // Check main hand and off hand?
            // The requirement says "Trang bị khi ép thuốc" (Equipment when enchanted).
            // Usually implies armor or things you "equip".
            // But main hand items can also be "invisible" (like swords).
            // So we should check contents.
            ItemStack[] contents = player.getInventory().getContents();
            changed = false;
            for (int i = 0; i < contents.length; i++) {
                if (checkAndExpire(contents[i], now)) {
                    changed = true;
                }
            }
            if (changed) {
                player.getInventory().setContents(contents);
            }
        }
    }

    private boolean checkAndExpire(ItemStack item, long now) {
        if (item == null || !ItemDataUtil.isInvisible(item))
            return false;

        long expiration = ItemDataUtil.getExpiration(item);
        if (expiration == -1) // Permanent
            return false;

        if (now > expiration) {
            ItemDataUtil.removeInvisible(item);
            removeInvisibleLore(item);
            return true;
        }
        return false;
    }

    private void removeInvisibleLore(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();

        // Remove Glint
        if (configManager.isEnchantGlint() && meta.hasEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING)) {
            meta.removeEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING);
            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        if (!meta.hasLore()) {
            item.setItemMeta(meta);
            return;
        }

        List<Component> lore = meta.lore();
        List<Component> newLore = new ArrayList<>();

        String invisibleLoreConfig = configManager.getInvisibleLore();
        String plainInvisible = ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(
                LegacyComponentSerializer.legacySection().deserialize(ColorUtil.colorize(invisibleLoreConfig))));

        String expirationLoreConfig = configManager.getExpirationLore();
        // Remove %date% to get the prefix
        String expirationPrefix = expirationLoreConfig.contains("%date%")
                ? expirationLoreConfig.split("%date%")[0]
                : expirationLoreConfig;
        String plainExpirationPrefix = ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(
                LegacyComponentSerializer.legacySection().deserialize(ColorUtil.colorize(expirationPrefix))));

        for (Component line : lore) {
            String serialized = LegacyComponentSerializer.legacySection().serialize(line);
            String plainLine = ChatColor.stripColor(serialized);

            if (plainLine.equals(plainInvisible)) {
                continue;
            }
            if (!plainExpirationPrefix.isEmpty() && plainLine.startsWith(plainExpirationPrefix)) {
                continue;
            }
            newLore.add(line);
        }
        meta.lore(newLore);
        item.setItemMeta(meta);
    }
}
