package com.NguyenDevs.extraInvisiblePotion.projectile;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileInvisibilityListener implements Listener {

    private final ConfigManager configManager;

    public ProjectileInvisibilityListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player))
            return;
        Projectile projectile = event.getEntity();

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        boolean mainHandInvisible = isInvisibleProjectileItem(mainHand);
        boolean offHandInvisible = isInvisibleProjectileItem(offHand);

        if (mainHandInvisible || offHandInvisible) {
            projectile.setInvisible(true);
        }
    }

    private boolean isInvisibleProjectileItem(ItemStack item) {
        if (item == null)
            return false;
        if (!configManager.getProjectileItemMaterials().contains(item.getType()))
            return false;
        return ItemDataUtil.isInvisible(item);
    }
}
