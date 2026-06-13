package com.everterra.tpa.listener;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.core.RequestManager;
import com.everterra.tpa.gui.JavaTpaGui;
import com.everterra.tpa.i18n.LangManager;
import com.everterra.tpa.teleport.TeleportScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Global event listener for TPA-related events.
 */
public class PlayerListener implements Listener {

    private final EverTerraTPA plugin;
    private final RequestManager requestManager;
    private final TeleportScheduler teleportScheduler;
    private final LangManager lang;

    public PlayerListener(EverTerraTPA plugin) {
        this.plugin = plugin;
        this.requestManager = plugin.getRequestManager();
        this.teleportScheduler = plugin.getTeleportScheduler();
        this.lang = plugin.getLangManager();
    }

    /**
     * Clean up on player quit: cancel incoming/outgoing requests,
     * cancel active teleport, notify other party.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Cancel any active teleport
        teleportScheduler.cancelOnQuit(player);

        // Notify other party about cancelled outgoing request
        var outgoing = requestManager.getOutgoingRequest(player.getUniqueId());
        if (outgoing != null) {
            Player target = Bukkit.getPlayer(outgoing.getTarget());
            if (target != null && target.isOnline()) {
                target.sendMessage(lang.format(target, "tpa.cancelled_sender",
                        java.util.Map.of("player", player.getName())));
            }
        }

        // Notify sender about cancelled incoming request
        var incoming = requestManager.getPendingRequest(player.getUniqueId());
        if (incoming != null) {
            Player sender = Bukkit.getPlayer(incoming.getSender());
            if (sender != null && sender.isOnline()) {
                sender.sendMessage(lang.format(sender, "tpa.cancelled_sender",
                        java.util.Map.of("player", player.getName())));
            }
        }

        // Remove all requests for this player
        requestManager.removeAllForPlayer(player.getUniqueId());
    }

    /**
     * Handle Java GUI inventory clicks (accept/deny buttons).
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        String title = event.getView().getTitle();
        String acceptTitle = lang.get(player, "gui.accept_title");

        // Only handle our GUI
        if (!title.equals(acceptTitle)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot == JavaTpaGui.getAcceptSlot()) {
            player.closeInventory();
            player.performCommand("tpaccept");
        } else if (slot == JavaTpaGui.getDenySlot()) {
            player.closeInventory();
            player.performCommand("tpadeny");
        }
    }

    /**
     * Cancel teleport on death.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        teleportScheduler.cancelOnDeath(event.getEntity());
    }

    /**
     * Cancel teleport on damage (configurable).
     * Note: move-based cancellation is handled inside TeleportScheduler.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            teleportScheduler.cancelOnDamage(player);
        }
    }
}
