package com.NguyenDevs.extraInvisiblePotion.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class EIPTabCompleter implements TabCompleter {

    private static final List<String> ADMIN_SUBS = List.of("reload", "enchant");
    private static final List<String> USER_SUBS = List.of("enchant");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> available = sender.hasPermission("extrainvisiblepotion.admin") ? ADMIN_SUBS : USER_SUBS;
            List<String> completions = new ArrayList<>();
            for (String sub : available) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
