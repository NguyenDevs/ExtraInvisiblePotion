package com.NguyenDevs.extraInvisiblePotion.gui;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.util.ColorUtil;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.event.inventory.InventoryType;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InvisibleCraftingGui {

    public static final int SLOT_EQUIPMENT = 0;
    public static final int SLOT_FILLER_1 = 1;
    public static final int SLOT_POTION = 2;
    public static final int SLOT_FILLER_2 = 3;
    public static final int SLOT_RESULT = 4;

    private static final String GUI_TITLE_RAW = "<gradient:#7B2FBE:#E040FB>✦ Invisible Crafting ✦</gradient>";

    private final ConfigManager configManager;

    public InvisibleCraftingGui(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void open(Player player) {
        Component title = LegacyComponentSerializer.legacySection()
                .deserialize(ColorUtil.colorize(GUI_TITLE_RAW));
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, title);
        inv.setItem(SLOT_FILLER_1, createFiller());
        inv.setItem(SLOT_FILLER_2, createFiller());
        player.openInventory(inv);
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f);
    }

    public boolean isGuiInventory(Inventory inv) {
        if (inv == null || inv.getType() != InventoryType.HOPPER)
            return false;
        if (inv.getViewers().isEmpty())
            return false;
        Component title = inv.getViewers().get(0).getOpenInventory().title();
        String legacy = LegacyComponentSerializer.legacySection().serialize(title);
        return ChatColor.stripColor(legacy).contains("Invisible Crafting");
    }

    public boolean isGui(InventoryView view) {
        if (view == null)
            return false;
        if (view.getTopInventory().getType() != InventoryType.HOPPER)
            return false;
        Component title = view.title();
        String legacy = LegacyComponentSerializer.legacySection().serialize(title);
        return ChatColor.stripColor(legacy).contains("Invisible Crafting");
    }

    public void updateResult(Inventory inv) {
        ItemStack equipment = inv.getItem(SLOT_EQUIPMENT);
        ItemStack potion = inv.getItem(SLOT_POTION);

        if (!isValidEquipment(equipment) || !isValidInvisiblePotion(potion)) {
            inv.setItem(SLOT_RESULT, null);
            return;
        }

        if (ItemDataUtil.isInvisible(equipment)) {
            inv.setItem(SLOT_RESULT, null);
            return;
        }

        ItemStack result = equipment.clone();
        // Allow stacking: maintain the amount from the equipment
        // result.setAmount(equipment.getAmount()); // Clone already keeps amount

        long duration = getInvisibilityDuration(potion);
        applyInvisibleTag(result, duration);
        inv.setItem(SLOT_RESULT, result);
    }

    private long getInvisibilityDuration(ItemStack potion) {
        if (potion == null || !potion.hasItemMeta())
            return -1;
        if (!(potion.getItemMeta() instanceof PotionMeta meta))
            return -1;

        if (meta.hasCustomEffects()) {
            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getType().equals(PotionEffectType.INVISIBILITY)) {
                    return effect.getDuration() * 50L;
                }
            }
        }

        PotionType type = meta.getBasePotionType();
        if (type != null) {
            for (PotionEffect effect : type.getPotionEffects()) {
                if (effect.getType().equals(PotionEffectType.INVISIBILITY)) {
                    return effect.getDuration() * 50L;
                }
            }
        }

        return -1;
    }

    public boolean isValidEquipment(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;
        Material mat = item.getType();
        if (mat.getMaxDurability() > 0)
            return true;
        if (configManager.getProjectileItemMaterials().contains(mat))
            return true;
        return isArmorOrWeapon(mat);
    }

    public boolean isValidInvisiblePotion(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;
        Set<Material> allowed = configManager.getInvisiblePotionMaterials();
        if (!allowed.contains(item.getType()))
            return false;
        if (!(item.getItemMeta() instanceof PotionMeta potionMeta))
            return false;

        PotionType baseType = potionMeta.getBasePotionType();
        if (baseType == PotionType.INVISIBILITY || baseType == PotionType.LONG_INVISIBILITY)
            return true;

        if (potionMeta.hasCustomEffects()) {
            for (PotionEffect effect : potionMeta.getCustomEffects()) {
                if (effect.getType().equals(PotionEffectType.INVISIBILITY))
                    return true;
            }
        }

        // Just in case checking for display name is needed as fallback
        if (item.getItemMeta().hasDisplayName()) {
            Component nameComp = item.getItemMeta().displayName();
            if (nameComp != null) {
                String legacy = LegacyComponentSerializer.legacySection().serialize(nameComp).toLowerCase();
                if (ChatColor.stripColor(legacy).contains("invisib"))
                    return true;
            }
        }

        return false;
    }

    private boolean isArmorOrWeapon(Material material) {
        String name = material.name();
        return name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("_PICKAXE")
                || name.endsWith("_SHOVEL") || name.endsWith("_HOE") || name.endsWith("_HELMET")
                || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS")
                || material == Material.SHIELD || material == Material.ELYTRA
                || material == Material.TRIDENT || material == Material.MACE
                || material == Material.SNOWBALL || material == Material.EGG
                || material == Material.ENDER_PEARL || material == Material.FISHING_ROD;
    }

    private void applyInvisibleTag(ItemStack item, long duration) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        meta.getPersistentDataContainer().set(ItemDataUtil.getInvisibleKey(),
                org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);

        List<Component> lore = meta.hasLore() ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.add(LegacyComponentSerializer.legacySection()
                .deserialize(ColorUtil.colorize(configManager.getInvisibleLore()))
                .decoration(TextDecoration.ITALIC, false));

        if (configManager.isDurationLogicEnabled() && duration > 0) {
            long expiration = System.currentTimeMillis() + duration;
            ItemDataUtil.setExpiration(item, expiration);

            String dateFormat = configManager.getDateFormat();
            String expirationLore = configManager.getExpirationLore();
            String dateStr = new java.text.SimpleDateFormat(dateFormat).format(new java.util.Date(expiration));

            lore.add(LegacyComponentSerializer.legacySection()
                    .deserialize(ColorUtil.colorize(expirationLore.replace("%date%", dateStr)))
                    .decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);

        if (configManager.isEnchantGlint()) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
    }

    private ItemStack createFiller() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.displayName(Component.empty());
        filler.setItemMeta(meta);
        return filler;
    }
}