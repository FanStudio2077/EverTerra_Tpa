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
 * /tpaccept — Accept a pending teleport request.
 */
public class TpacceptCommand implements CommandExecutor {

    private final EverTerraTPA plugin;
    private final RequestManager requestManager;
    private final LangManager lang;

    public TpacceptCommand(EverTerraTPA plugin, RequestManager requestManager) {
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

        // Check for pending request
        TpaRequest request = requestManager.acceptRequest(player);
        if (request == null) {
            player.sendMessage(lang.format(player, "tpa.no_request"));
            return true;
        }

        // Notify sender
        Player requester = Bukkit.getPlayer(request.getSender());
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(lang.format(requester, "tpa.accepted_sender",
                    Map.of("player", player.getName())));
        }

        player.sendMessage(lang.format(player, "tpa.accepted"));

        // Queue teleport (Phase 4 will add delay scheduler)
        // For now, instant teleport
        executeTeleport(request);

        return true;
    }

    /**
     * Executes the teleport immediately (Phase 4 will replace with delayed teleport).
     */
    private void executeTeleport(TpaRequest request) {
        Player target = Bukkit.getPlayer(request.getTarget());
        Player requester = Bukkit.getPlayer(request.getSender());

        if (target == null || requester == null) return;

        switch (request.getType()) {
            case TPA -> requester.teleport(target.getLocation());
            case TPAC -> target.teleport(requester.getLocation());
        }
    }
}
