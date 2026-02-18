package com.NguyenDevs.extraInvisiblePotion.anvil;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.util.ColorUtil;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
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

    public AnvilInvisibleListener(ConfigManager configManager, MessageManager messageManager) {
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack firstItem = inv.getFirstItem();
        ItemStack secondItem = inv.getSecondItem();

        ItemStack equipment = null;

        if (isValidEquipment(firstItem) && isValidInvisiblePotion(secondItem)) {
            equipment = firstItem;
        } else if (isValidEquipment(secondItem) && isValidInvisiblePotion(firstItem)) {
            equipment = secondItem;
        }

        if (equipment == null)
            return;

        if (ItemDataUtil.isInvisible(equipment)) {
            event.setResult(null);
            return;
        }

        ItemStack result = equipment.clone();
        applyInvisibleTag(result);
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

        if (!player.hasPermission("extrainvisiblepotion.use")) {
            event.setCancelled(true);
            player.sendMessage(messageManager.getMessage("no-permission"));
            return;
        }

        player.sendMessage(messageManager.getMessage("anvil-crafted"));
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
        if (baseType == PotionType.INVISIBILITY)
            return true;

        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            if (effect.getType().equals(PotionEffectType.INVISIBILITY))
                return true;
        }

        if (baseType == null && item.getItemMeta().hasDisplayName()) {
            Component nameComp = item.getItemMeta().displayName();
            if (nameComp != null) {
                String plain = LegacyComponentSerializer.legacySection().serialize(nameComp).toLowerCase();
                if (plain.contains("invisib"))
                    return true;
            }
        }

        return false;
    }

    private void applyInvisibleTag(ItemStack item) {
        ItemDataUtil.setInvisible(item);
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        List<Component> lore = meta.hasLore() ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.add(LegacyComponentSerializer.legacySection()
                .deserialize(ColorUtil.colorize(configManager.getInvisibleLore())));
        meta.lore(lore);

        if (configManager.isEnchantGlint()) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
    }
}
