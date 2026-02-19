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
            List<String> available = new ArrayList<>();
            if (sender.hasPermission("extrainvisiblepotion.admin")) {
                available.addAll(ADMIN_SUBS);
                available.add("setinvisible");
            } else {
                available.addAll(USER_SUBS);
            }

            List<String> completions = new ArrayList<>();
            for (String sub : available) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setinvisible")) {
            if (!sender.hasPermission("extrainvisiblepotion.admin"))
                return new ArrayList<>();
            List<String> bools = List.of("true", "false");
            List<String> completions = new ArrayList<>();
            for (String b : bools) {
                if (b.startsWith(args[1].toLowerCase()))
                    completions.add(b);
            }
            return completions;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setinvisible") && args[1].equalsIgnoreCase("true")) {
            if (!sender.hasPermission("extrainvisiblepotion.admin"))
                return new ArrayList<>();
            return List.of("1d", "1h", "30m", "10s", "-1");
        }

        return new ArrayList<>();
    }
}
