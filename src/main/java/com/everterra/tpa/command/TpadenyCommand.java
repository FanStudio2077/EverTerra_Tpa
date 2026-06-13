package com.everterra.tpa.command;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.core.RequestManager;
import com.everterra.tpa.core.TpaRequest;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * /tpadeny — Deny a pending teleport request.
 */
public class TpadenyCommand implements CommandExecutor {

    private final EverTerraTPA plugin;
    private final RequestManager requestManager;
    private final LangManager lang;

    public TpadenyCommand(EverTerraTPA plugin, RequestManager requestManager) {
        this.plugin = plugin;
        this.requestManager = requestManager;
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

        TpaRequest request = requestManager.denyRequest(player);
        if (request == null) {
            player.sendMessage(lang.format(player, "tpa.no_request"));
            return true;
        }

        // Notify sender
        Player requester = Bukkit.getPlayer(request.getSender());
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(lang.format(requester, "tpa.denied_sender",
                    Map.of("player", player.getName())));
        }

        player.sendMessage(lang.format(player, "tpa.denied"));

        return true;
    }
}
