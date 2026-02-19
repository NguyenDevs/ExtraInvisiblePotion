package com.NguyenDevs.extraInvisiblePotion.command;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReloadCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final InvisibleCraftingGui craftingGui;

    public ReloadCommand(ConfigManager configManager, MessageManager messageManager, InvisibleCraftingGui craftingGui) {
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.craftingGui = craftingGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("extrainvisiblepotion.admin")) {
                sender.sendMessage(messageManager.getMessage("no-permission"));
                return true;
            }
            configManager.reload();
            messageManager.reload();
            sender.sendMessage(messageManager.getMessage("reload-success"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("enchant")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(messageManager.getMessage("player-only"));
                return true;
            }
            if (!player.hasPermission("extrainvisiblepotion.command.enchant")) {
                player.sendMessage(messageManager.getMessage("no-permission"));
                return true;
            }
            craftingGui.open(player);
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("setinvisible")) {
            if (!sender.hasPermission("extrainvisiblepotion.admin")) {
                sender.sendMessage(messageManager.getMessage("no-permission"));
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(messageManager.getMessage("player-only"));
                return true;
            }

            boolean setInvisible = Boolean.parseBoolean(args[1]);
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType() == org.bukkit.Material.AIR) {
                player.sendMessage(ChatColor.RED + "You must hold an item in your main hand.");
                return true;
            }

            if (setInvisible) {
                long duration = -1;
                if (args.length >= 3) {
                    duration = parseDuration(args[2]);
                }

                applyInvisible(item, duration);
                player.sendMessage(
                        ChatColor.GREEN + "Item set to invisible" + (duration > 0 ? " with duration." : "."));
            } else {
                removeInvisible(item);
                player.sendMessage(ChatColor.GREEN + "Item invisible status removed.");
            }
            return true;
        }

        sender.sendMessage(messageManager.getMessage("unknown-command"));
        return true;
    }

    private long parseDuration(String input) {
        if (input.equals("-1"))
            return -1;

        try {
            long multiplier = 1000;
            if (input.endsWith("d"))
                multiplier *= 24 * 60 * 60;
            else if (input.endsWith("h"))
                multiplier *= 60 * 60;
            else if (input.endsWith("m"))
                multiplier *= 60;
            else if (input.endsWith("s"))
                multiplier *= 1;
            else
                return -1;

            String numberPart = input.substring(0, input.length() - 1);
            return Long.parseLong(numberPart) * multiplier;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void applyInvisible(ItemStack item, long duration) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        meta.getPersistentDataContainer().set(com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil.getInvisibleKey(),
                org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);

        java.util.List<net.kyori.adventure.text.Component> lore = meta.hasLore()
                ? new java.util.ArrayList<>(meta.lore())
                : new java.util.ArrayList<>();
        lore.add(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .deserialize(
                        com.NguyenDevs.extraInvisiblePotion.util.ColorUtil.colorize(configManager.getInvisibleLore()))
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));

        if (configManager.isDurationLogicEnabled() && duration > 0) {
            long expiration = System.currentTimeMillis() + duration;
            com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil.setExpiration(item, expiration);

            String dateFormat = configManager.getDateFormat();
            String expirationLore = configManager.getExpirationLore();
            String dateStr = new java.text.SimpleDateFormat(dateFormat).format(new java.util.Date(expiration));

            lore.add(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                    .deserialize(com.NguyenDevs.extraInvisiblePotion.util.ColorUtil
                            .colorize(expirationLore.replace("%date%", dateStr)))
                    .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
        }

        meta.lore(lore);

        if (configManager.isEnchantGlint()) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
    }

    private void removeInvisible(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil.removeInvisible(item);

        // Remove Glint
        if (configManager.isEnchantGlint() && meta.hasEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING)) {
            meta.removeEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING);
            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        if (!meta.hasLore()) {
            item.setItemMeta(meta);
            return;
        }

        java.util.List<net.kyori.adventure.text.Component> lore = meta.lore();
        java.util.List<net.kyori.adventure.text.Component> newLore = new java.util.ArrayList<>();

        String invisibleLoreConfig = configManager.getInvisibleLore();
        String plainInvisible = org.bukkit.ChatColor.stripColor(
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(
                        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                                .deserialize(com.NguyenDevs.extraInvisiblePotion.util.ColorUtil
                                        .colorize(invisibleLoreConfig))));

        String expirationLoreConfig = configManager.getExpirationLore();
        String expirationPrefix = expirationLoreConfig.contains("%date%")
                ? expirationLoreConfig.split("%date%")[0]
                : expirationLoreConfig;
        String plainExpirationPrefix = org.bukkit.ChatColor.stripColor(
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(
                        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                                .deserialize(com.NguyenDevs.extraInvisiblePotion.util.ColorUtil
                                        .colorize(expirationPrefix))));

        for (net.kyori.adventure.text.Component line : lore) {
            String serialized = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                    .serialize(line);
            String plainLine = org.bukkit.ChatColor.stripColor(serialized);

            if (plainLine.equals(plainInvisible)) {
                continue;
            }
            if (!plainExpirationPrefix.isEmpty() && plainLine.startsWith(plainExpirationPrefix)) {
                continue;
            }
            newLore.add(line);
        }
        meta.lore(newLore);
        item.setItemMeta(meta);
    }
}
