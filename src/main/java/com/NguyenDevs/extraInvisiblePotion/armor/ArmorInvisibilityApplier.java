package com.NguyenDevs.extraInvisiblePotion.armor;

import com.NguyenDevs.extraInvisiblePotion.data.PlayerDataManager;
import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorInvisibilityApplier {

    private final PlayerDataManager playerDataManager;

    public ArmorInvisibilityApplier(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void apply(Player player) {
        playerDataManager.addInvisibleArmorPlayer(player.getUniqueId());
        applyEffect(player);
    }

    public void remove(Player player) {
        if (!hasAnyInvisibleArmor(player)) {
            playerDataManager.removeInvisibleArmorPlayer(player.getUniqueId());
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    public void reapplyIfNeeded(Player player) {
        if (hasAnyInvisibleArmor(player)) {
            playerDataManager.addInvisibleArmorPlayer(player.getUniqueId());
            applyEffect(player);
        } else {
            playerDataManager.removeInvisibleArmorPlayer(player.getUniqueId());
        }
    }

    private void applyEffect(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false));
    }

    private boolean hasAnyInvisibleArmor(Player player) {
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (ItemDataUtil.isInvisible(piece))
                return true;
        }
        return false;
    }
}
