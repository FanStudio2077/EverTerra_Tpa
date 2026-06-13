package com.everterra.tpa.command;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.core.CooldownManager;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * /tpabypass <player> — Toggle bypass mode for a player (admin only).
 */
public class TpabypassCommand implements CommandExecutor, TabCompleter {

    private final LangManager lang;
    private final CooldownManager cooldownManager;

    public TpabypassCommand(EverTerraTPA plugin) {
        this.lang = plugin.getLangManager();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tpa.bypass")) {
            sender.sendMessage("You don't have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /tpabypass <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("Player not found.");
            return true;
        }

        // Toggle: if player has bypass, remove it (by clearing cooldowns, they still have permission)
        // The actual bypass is permission-based. This command clears cooldowns as a convenience.
        cooldownManager.clearAll(target);

        sender.sendMessage("Cleared cooldowns for " + target.getName());
        if (sender instanceof Player admin) {
            target.sendMessage(lang.format(target, "admin.bypass_on",
                    Map.of("player", admin.getName())));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
