package com.fast.ghost;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FastGhostCommand implements CommandExecutor, TabCompleter {

    private final FastGhost plugin;
    private final Stats stats;

    public FastGhostCommand(FastGhost plugin, Stats stats) {
        this.plugin = plugin;
        this.stats = stats;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(prefix() + ChatColor.YELLOW + "Usage: /fastghost <status|reload>");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            if (!sender.hasPermission("fastghost.status")) {
                sender.sendMessage(prefix() + color(plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            showStatus(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("fastghost.admin")) {
                sender.sendMessage(prefix() + color(plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            boolean ok = plugin.reloadPluginConfig();
            if (ok) {
                sender.sendMessage(prefix() + color(plugin.getConfig().getString("messages.reload", "&aReloaded.")));
            } else {
                sender.sendMessage(prefix() + ChatColor.RED + "Reload failed!");
            }
            return true;
        }

        sender.sendMessage(prefix() + ChatColor.YELLOW + "Usage: /fastghost <status|reload>");
        return true;
    }

    private void showStatus(CommandSender sender) {
        List<String> lines = plugin.getConfig().getStringList("messages.status");
        boolean enabled = plugin.getConfig().getBoolean("enabled", true);
        String status = enabled ? "&aENABLED" : "&cDISABLED";

        for (String raw : lines) {
            String line = raw
                    .replace("%status%", status)
                    .replace("%received%", String.valueOf(stats.getHitsReceived()))
                    .replace("%cancelled%", String.valueOf(stats.getHitsCancelled()))
                    .replace("%fixed%", String.valueOf(stats.getGhostHitsFixed()))
                    .replace("%logged%", String.valueOf(stats.getHitsLogged()));
            sender.sendMessage(color(line));
        }
    }

    private String prefix() {
        return color(plugin.getConfig().getString("messages.prefix", "&8[&6FastGhost&8] "));
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && command.getName().equalsIgnoreCase("fastghost")) {
            String typed = args[0].toLowerCase();
            List<String> options = new ArrayList<>();
            if ("status".startsWith(typed)) options.add("status");
            if ("reload".startsWith(typed)) options.add("reload");
            return options;
        }
        return Collections.emptyList();
    }
}
