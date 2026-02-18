package com.NguyenDevs.extraInvisiblePotion.gui;

import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
        
        InventoryView view = event.getView();
        if (!gui.isGui(view))
            return;

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }

        Inventory topInv = view.getTopInventory();
        Inventory clickedInv = event.getClickedInventory();
        
        if (clickedInv == null) return;

        int slot = event.getRawSlot();
        int guiSize = topInv.getSize();

        if (slot >= guiSize) {
            InventoryAction action = event.getAction();
            if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                ItemStack currentItem = event.getCurrentItem();
                if (currentItem == null || currentItem.getType() == Material.AIR) return;

                if (gui.isValidEquipment(currentItem)) {
                    ItemStack existing = topInv.getItem(InvisibleCraftingGui.SLOT_EQUIPMENT);
                    if (existing == null || existing.getType() == Material.AIR) {
                        topInv.setItem(InvisibleCraftingGui.SLOT_EQUIPMENT, currentItem);
                        event.setCurrentItem(null);
                        scheduleUpdateResult(topInv);
                    }
                } else if (gui.isValidInvisiblePotion(currentItem)) {
                    ItemStack existing = topInv.getItem(InvisibleCraftingGui.SLOT_POTION);
                    if (existing == null || existing.getType() == Material.AIR) {
                        topInv.setItem(InvisibleCraftingGui.SLOT_POTION, currentItem);
                        event.setCurrentItem(null);
                        scheduleUpdateResult(topInv);
                    }
                }
                return;
            }
            return;
        }

        if (slot == InvisibleCraftingGui.SLOT_FILLER_1 || slot == InvisibleCraftingGui.SLOT_FILLER_2) {
            event.setCancelled(true);
            return;
        }

        if (slot == InvisibleCraftingGui.SLOT_RESULT) {
            event.setCancelled(true);
            ClickType click = event.getClick();
            if (click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
                handleResultClick(player, topInv);
            }
            return;
        }

        if (slot == InvisibleCraftingGui.SLOT_EQUIPMENT || slot == InvisibleCraftingGui.SLOT_POTION) {
            ClickType click = event.getClick();
            if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                ItemStack item = topInv.getItem(slot);
                if (item != null && item.getType() != Material.AIR) {
                    topInv.setItem(slot, null);
                    giveOrDrop(player, item);
                    scheduleUpdateResult(topInv);
                }
                return;
            }

            scheduleUpdateResult(topInv);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        
        InventoryView view = event.getView();
        if (!gui.isGui(view))
            return;

        Inventory topInv = view.getTopInventory();
        for (int slot : event.getRawSlots()) {
            if (slot < topInv.getSize()) {
                if (slot != InvisibleCraftingGui.SLOT_EQUIPMENT
                        && slot != InvisibleCraftingGui.SLOT_POTION) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        scheduleUpdateResult(topInv);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player))
            return;
        
        InventoryView view = event.getView();
        if (!gui.isGui(view))
            return;

        Inventory topInv = view.getTopInventory();
        returnItemToPlayer(player, topInv, InvisibleCraftingGui.SLOT_EQUIPMENT);
        returnItemToPlayer(player, topInv, InvisibleCraftingGui.SLOT_POTION);
        topInv.setItem(InvisibleCraftingGui.SLOT_RESULT, null);
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

    private void scheduleUpdateResult(Inventory inv) {
        org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("ExtraInvisiblePotion");
        if (plugin != null) {
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> gui.updateResult(inv));
        }
    }

    private void giveOrDrop(Player player, ItemStack item) {
        var leftover = player.getInventory().addItem(item);
        leftover.values().forEach(drop -> player.getWorld().dropItemNaturally(player.getLocation(), drop));
    }
}