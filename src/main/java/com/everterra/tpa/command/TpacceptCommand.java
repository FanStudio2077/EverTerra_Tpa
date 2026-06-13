package com.everterra.tpa.command;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.core.RequestManager;
import com.everterra.tpa.core.TpaRequest;
import com.everterra.tpa.economy.EconomyManager;
import com.everterra.tpa.i18n.LangManager;
import com.everterra.tpa.teleport.TeleportScheduler;
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
    private final TeleportScheduler teleportScheduler;
    private final EconomyManager economyManager;

    public TpacceptCommand(EverTerraTPA plugin, RequestManager requestManager) {
        this.plugin = plugin;
        this.requestManager = requestManager;
        this.lang = plugin.getLangManager();
        this.teleportScheduler = plugin.getTeleportScheduler();
        this.economyManager = plugin.getEconomyManager();
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

        // Peek the pending request (don't remove yet)
        TpaRequest request = requestManager.getPendingRequest(player.getUniqueId());
        if (request == null) {
            player.sendMessage(lang.format(player, "tpa.no_request"));
            return true;
        }

        // Economy check: charge the requester (the one who sent the request)
        Player requester = Bukkit.getPlayer(request.getSender());
        if (economyManager.isEnabled() && requester != null && requester.isOnline()) {
            double cost = economyManager.getCost(request.getType());
            if (!economyManager.canAfford(requester, request.getType())) {
                double balance = economyManager.getBalance(requester);
                requester.sendMessage(lang.format(requester, "error.no_money",
                        Map.of("cost", economyManager.format(cost),
                               "balance", economyManager.format(balance))));
                player.sendMessage(lang.format(player, "error.no_money",
                        Map.of("cost", economyManager.format(cost),
                               "balance", economyManager.format(balance))));
                return true;
            }

            // Charge the requester
            economyManager.charge(requester, request.getType());
            requester.sendMessage(lang.format(requester, "error.cost",
                    Map.of("cost", economyManager.format(cost))));
        }

        // Now accept (remove) the request
        request = requestManager.acceptRequest(player);
        if (request == null) {
            // Race condition - request expired between peek and accept
            player.sendMessage(lang.format(player, "tpa.no_request"));
            return true;
        }

        // Notify sender (requester)
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(lang.format(requester, "tpa.accepted_sender",
                    Map.of("player", player.getName())));
        }

        player.sendMessage(lang.format(player, "tpa.accepted"));

        // Schedule delayed teleport
        teleportScheduler.scheduleTeleport(request);

        return true;
    }
}
