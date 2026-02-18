package com.NguyenDevs.extraInvisiblePotion.armor;

import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipListener implements Listener {

    private final ArmorInvisibilityApplier applier;

    public ArmorEquipListener(ArmorInvisibilityApplier applier) {
        this.applier = applier;
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        boolean newIsInvisible = ItemDataUtil.isInvisible(newItem);
        boolean oldIsInvisible = ItemDataUtil.isInvisible(oldItem);

        if (newIsInvisible && !oldIsInvisible) {
            applier.apply(player);
        } else if (!newIsInvisible && oldIsInvisible) {
            applier.remove(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        applier.reapplyIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        applier.reapplyIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        applier.remove(event.getPlayer());
    }
}
