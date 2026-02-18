package com.NguyenDevs.extraInvisiblePotion.item;

import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemInvisibilityListener implements Listener {

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        if (ItemDataUtil.isInvisible(newItem)) {
            hideItemFromOthers(player, newItem);
        } else if (ItemDataUtil.isInvisible(oldItem)) {
            showItemToOthers(player, newItem);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        if (ItemDataUtil.isInvisible(held)) {
            hideItemFromOthers(player, held);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Player other : player.getWorld().getPlayers()) {
            if (!other.equals(player)) {
                other.sendEquipmentChange(player, EquipmentSlot.HAND, player.getInventory().getItemInMainHand());
            }
        }
    }

    private void hideItemFromOthers(Player player, ItemStack item) {
        for (Player other : player.getWorld().getPlayers()) {
            if (!other.equals(player)) {
                other.sendEquipmentChange(player, EquipmentSlot.HAND, null);
            }
        }
    }

    private void showItemToOthers(Player player, ItemStack item) {
        for (Player other : player.getWorld().getPlayers()) {
            if (!other.equals(player)) {
                other.sendEquipmentChange(player, EquipmentSlot.HAND, item);
            }
        }
    }
}
