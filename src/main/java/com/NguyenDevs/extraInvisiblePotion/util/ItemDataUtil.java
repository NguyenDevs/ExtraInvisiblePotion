package com.NguyenDevs.extraInvisiblePotion.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ItemDataUtil {

    private static final String INVISIBLE_KEY = "invisible";
    private static NamespacedKey invisibleKey;

    public static void init(Plugin plugin) {
        invisibleKey = new NamespacedKey(plugin, INVISIBLE_KEY);
    }

    public static void setInvisible(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(invisibleKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    public static boolean isInvisible(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return false;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(invisibleKey, PersistentDataType.BYTE);
    }

    public static NamespacedKey getInvisibleKey() {
        return invisibleKey;
    }
}
