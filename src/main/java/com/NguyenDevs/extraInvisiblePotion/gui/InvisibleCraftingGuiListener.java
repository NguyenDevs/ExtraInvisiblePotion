package com.NguyenDevs.extraInvisiblePotion.gui;

import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvisibleCraftingGuiListener implements Listener {

    private final InvisibleCraftingGui gui;
    private final MessageManager messageManager;

    public InvisibleCraftingGuiListener(InvisibleCraftingGui gui, MessageManager messageManager) {
        this.gui = gui;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;
        Inventory inv = event.getInventory();
        if (!gui.isGuiInventory(inv))
            return;

        int slot = event.getRawSlot();

        boolean isFiller = slot != InvisibleCraftingGui.SLOT_EQUIPMENT
                && slot != InvisibleCraftingGui.SLOT_POTION
                && slot != InvisibleCraftingGui.SLOT_RESULT
                && slot < inv.getSize();

        if (isFiller) {
            event.setCancelled(true);
            return;
        }

        if (slot == InvisibleCraftingGui.SLOT_RESULT) {
            event.setCancelled(true);
            handleResultClick(player, inv);
            return;
        }

        if (slot >= inv.getSize())
            return;

        org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("ExtraInvisiblePotion");
        if (plugin != null) {
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> gui.updateResult(inv));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Inventory inv = event.getInventory();
        if (!gui.isGuiInventory(inv))
            return;

        for (int slot : event.getRawSlots()) {
            if (slot < inv.getSize()
                    && slot != InvisibleCraftingGui.SLOT_EQUIPMENT
                    && slot != InvisibleCraftingGui.SLOT_POTION) {
                event.setCancelled(true);
                return;
            }
        }

        org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("ExtraInvisiblePotion");
        if (plugin != null) {
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> gui.updateResult(inv));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player))
            return;
        Inventory inv = event.getInventory();
        if (!gui.isGuiInventory(inv))
            return;

        returnItemToPlayer(player, inv, InvisibleCraftingGui.SLOT_EQUIPMENT);
        returnItemToPlayer(player, inv, InvisibleCraftingGui.SLOT_POTION);
        returnItemToPlayer(player, inv, InvisibleCraftingGui.SLOT_RESULT);
    }

    private void handleResultClick(Player player, Inventory inv) {
        ItemStack result = inv.getItem(InvisibleCraftingGui.SLOT_RESULT);
        if (result == null || result.getType() == Material.AIR)
            return;
        if (!ItemDataUtil.isInvisible(result))
            return;

        if (!player.hasPermission("extrainvisiblepotion.use")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return;
        }

        ItemStack equipment = inv.getItem(InvisibleCraftingGui.SLOT_EQUIPMENT);
        ItemStack potion = inv.getItem(InvisibleCraftingGui.SLOT_POTION);

        if (equipment != null && equipment.getAmount() > 1) {
            equipment.setAmount(equipment.getAmount() - 1);
        } else {
            inv.setItem(InvisibleCraftingGui.SLOT_EQUIPMENT, null);
        }

        if (potion != null && potion.getAmount() > 1) {
            potion.setAmount(potion.getAmount() - 1);
        } else {
            inv.setItem(InvisibleCraftingGui.SLOT_POTION, null);
        }

        inv.setItem(InvisibleCraftingGui.SLOT_RESULT, null);

        giveOrDrop(player, result);
        player.sendMessage(messageManager.getMessage("anvil-crafted"));

        gui.updateResult(inv);
    }

    private void returnItemToPlayer(Player player, Inventory inv, int slot) {
        ItemStack item = inv.getItem(slot);
        if (item == null || item.getType() == Material.AIR)
            return;
        inv.setItem(slot, null);
        giveOrDrop(player, item);
    }

    private void giveOrDrop(Player player, ItemStack item) {
        var leftover = player.getInventory().addItem(item);
        leftover.values().forEach(drop -> player.getWorld().dropItemNaturally(player.getLocation(), drop));
    }
}
