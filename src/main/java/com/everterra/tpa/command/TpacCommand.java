package com.everterra.tpa.command;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.core.CooldownManager;
import com.everterra.tpa.core.RequestManager;
import com.everterra.tpa.core.TpaType;
import com.everterra.tpa.gui.TpaGuiManager;
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
 * /tpac <player> — Send a teleport-here request (invite player to come to you).
 */
public class TpacCommand implements CommandExecutor, TabCompleter {

    private final EverTerraTPA plugin;
    private final RequestManager requestManager;
    private final CooldownManager cooldownManager;
    private final TpaGuiManager guiManager;
    private final LangManager lang;

    public TpacCommand(EverTerraTPA plugin, RequestManager requestManager) {
        this.plugin = plugin;
        this.requestManager = requestManager;
        this.cooldownManager = plugin.getCooldownManager();
        this.guiManager = plugin.getGuiManager();
        this.lang = plugin.getLangManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("tpa.use")) {
            player.sendMessage(lang.format(player, "error.no_permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(lang.format(player, "tpa.sent") + " §7Usage: /tpac <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(lang.format(player, "error.player_not_found"));
            return true;
        }

        // Check cooldown
        long cooldownRemaining = cooldownManager.getRemaining(player, TpaType.TPAC);
        if (cooldownRemaining > 0) {
            player.sendMessage(lang.format(player, "error.cooldown",
                    Map.of("time", cooldownRemaining)));
            return true;
        }

        String error = requestManager.createRequest(player, target, TpaType.TPAC);
        if (error != null) {
            player.sendMessage(lang.format(player, error));
            return true;
        }

        // Set cooldown
        cooldownManager.setCooldown(player, TpaType.TPAC);

        player.sendMessage(lang.format(player, "tpa.sent_here",
                Map.of("player", target.getName())));
        target.sendMessage(lang.format(target, "tpa.received_here",
                Map.of("player", player.getName())));

        // Show GUI to target
        guiManager.showReceiveGui(target,
                requestManager.getPendingRequest(target.getUniqueId()));

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
