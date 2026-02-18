package com.NguyenDevs.extraInvisiblePotion.command;

import com.NguyenDevs.extraInvisiblePotion.config.ConfigManager;
import com.NguyenDevs.extraInvisiblePotion.config.MessageManager;
import com.NguyenDevs.extraInvisiblePotion.gui.InvisibleCraftingGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            if (!player.hasPermission("extrainvisiblepotion.use")) {
                player.sendMessage(messageManager.getMessage("no-permission"));
                return true;
            }
            craftingGui.open(player);
            return true;
        }

        sender.sendMessage(messageManager.getMessage("unknown-command"));
        return true;
    }
}
