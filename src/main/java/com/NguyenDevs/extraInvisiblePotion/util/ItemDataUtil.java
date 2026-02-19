package com.NguyenDevs.extraInvisiblePotion.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ItemDataUtil {

    private static final String INVISIBLE_KEY = "invisible";
    private static final String EXPIRATION_KEY = "invisible_expiration";
    private static NamespacedKey invisibleKey;
    private static NamespacedKey expirationKey;

    public static void init(Plugin plugin) {
        invisibleKey = new NamespacedKey(plugin, INVISIBLE_KEY);
        expirationKey = new NamespacedKey(plugin, EXPIRATION_KEY);
    }

    public static void setInvisible(ItemStack item) {
        if (item == null)
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        meta.getPersistentDataContainer().set(invisibleKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    public static void setExpiration(ItemStack item, long expiration) {
        if (item == null)
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        meta.getPersistentDataContainer().set(expirationKey, PersistentDataType.LONG, expiration);
        item.setItemMeta(meta);
    }

    public static long getExpiration(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return -1;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(expirationKey, PersistentDataType.LONG))
            return -1;
        return pdc.get(expirationKey, PersistentDataType.LONG);
    }

    public static void removeInvisible(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(invisibleKey);
        pdc.remove(expirationKey);

        // Remove lore if needed, but we handle that in the task
        // Actually, we should probably remove the specific lore line here if possible,
        // but it's easier to just remove the tag. The task will likely clear the lore.
        // Let's just remove the tags for now.

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
