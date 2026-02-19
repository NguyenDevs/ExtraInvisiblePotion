package com.NguyenDevs.extraInvisiblePotion.listener;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGui;
import com.NguyenDevs.extraInvisiblePotion.util.ColorUtil;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
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

public class AnvilInvisibleListener implements Listener {

    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final InvisibleCraftingGui gui;

    public AnvilInvisibleListener(ConfigManager configManager, MessageManager messageManager,
            InvisibleCraftingGui gui) {
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.gui = gui;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!event.getPlayer().isSneaking())
            return;
        if (event.getClickedBlock() == null)
            return;

        Material type = event.getClickedBlock().getType();
        if (type != Material.ANVIL && type != Material.CHIPPED_ANVIL && type != Material.DAMAGED_ANVIL)
            return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!player.hasPermission("extrainvisiblepotion.anvil")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }

        gui.open(player);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack firstItem = inv.getFirstItem();
        ItemStack secondItem = inv.getSecondItem();

        ItemStack equipment = null;
        ItemStack potion = null;

        if (isValidEquipment(firstItem) && isValidInvisiblePotion(secondItem)) {
            equipment = firstItem;
            potion = secondItem;
        } else if (isValidEquipment(secondItem) && isValidInvisiblePotion(firstItem)) {
            equipment = secondItem;
            potion = firstItem;
        }

        if (equipment == null)
            return;

        if (ItemDataUtil.isInvisible(equipment)) {
            event.setResult(null);
            return;
        }

        ItemStack result = equipment.clone();
        // Maintain amount (clone does it)

        long duration = getInvisibilityDuration(potion);
        applyInvisibleTag(result, duration);
        event.setResult(result);
    }

    @EventHandler
    public void onAnvilTakeResult(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory inv))
            return;
        if (!(event.getWhoClicked() instanceof Player player))
            return;
        if (event.getRawSlot() != 2)
            return;

        ItemStack result = inv.getResult();
        if (result == null || !ItemDataUtil.isInvisible(result))
            return;

        if (!player.hasPermission("extrainvisiblepotion.anvil")) {
            event.setCancelled(true);
            player.sendMessage(messageManager.getMessage("no-permission"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }

        player.sendMessage(messageManager.getMessage("anvil-crafted"));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
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

    private boolean isValidEquipment(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;
        Material mat = item.getType();
        if (mat.getMaxDurability() > 0)
            return true;
        if (configManager.getProjectileItemMaterials().contains(mat))
            return true;
        return isArmorOrWeapon(mat);
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

    private boolean isValidInvisiblePotion(ItemStack item) {
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
}
