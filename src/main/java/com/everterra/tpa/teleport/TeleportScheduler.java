package com.everterra.tpa.teleport;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.core.TpaRequest;
import com.everterra.tpa.i18n.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles delayed teleport with countdown and cancel conditions.
 * Runs countdown on main thread, executes teleport at end.
 */
public class TeleportScheduler {

    private final EverTerraTPA plugin;
    private final ConfigManager config;
    private final LangManager lang;

    // Active teleport tasks: player UUID -> running task
    private final Map<UUID, BukkitTask> activeTasks = new ConcurrentHashMap<>();

    // Lock per-player to prevent teleport cancellation during move/damage
    private final Map<UUID, Boolean> cancelFlags = new ConcurrentHashMap<>();

    public TeleportScheduler(EverTerraTPA plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.lang = plugin.getLangManager();
    }

    /**
     * Schedules a delayed teleport for the given request.
     *
     * @param request The accepted TPA request
     */
    public void scheduleTeleport(TpaRequest request) {
        Player target = Bukkit.getPlayer(request.getTarget());
        Player requester = Bukkit.getPlayer(request.getSender());

        if (target == null || requester == null) return;

        // Determine who is being teleported
        Player teleportee;
        switch (request.getType()) {
            case TPA -> teleportee = requester;  // sender teleports to target
            case TPAC -> teleportee = target;     // target teleports to sender (requester)
            default -> { return; }
        }

        // Cancel any existing teleport for this player
        cancelTeleport(teleportee);

        int delay = config.getTeleportDelay();
        Location destination;

        switch (request.getType()) {
            case TPA -> destination = target.getLocation();
            case TPAC -> destination = requester.getLocation();
            default -> { return; }
        }

        // Store initial location for move detection
        final Location startLocation = teleportee.getLocation().clone();
        final UUID teleporteeUuid = teleportee.getUniqueId();
        cancelFlags.put(teleporteeUuid, false);

        // Send initial message
        teleportee.sendMessage(lang.format(teleportee, "tpa.teleporting",
                Map.of("time", delay)));

        // Start countdown
        BukkitTask task = new BukkitRunnable() {
            int countdown = delay;

            @Override
            public void run() {
                Player p = Bukkit.getPlayer(teleporteeUuid);
                if (p == null || !p.isOnline()) {
                    cancelTeleport(teleporteeUuid);
                    return;
                }

                // Check cancel flags
                if (Boolean.TRUE.equals(cancelFlags.get(teleporteeUuid))) {
                    cancelTeleport(teleporteeUuid);
                    return;
                }

                // Check if player moved
                if (config.isCancelOnMove() && hasMoved(p, startLocation)) {
                    cancelFlags.put(teleporteeUuid, true);
                    p.sendMessage(lang.format(p, "error.cancelled_move"));
                    cancelTeleport(teleporteeUuid);
                    return;
                }

                countdown--;

                if (countdown <= 0) {
                    // Execute teleport
                    if (p.isOnline()) {
                        p.teleport(destination);
                        p.sendMessage(lang.format(p, "tpa.teleported"));
                    }
                    cancelTeleport(teleporteeUuid);
                } else {
                    // Countdown tick message (optional action bar)
                    p.sendActionBar(Component.text(
                            lang.get(p, "tpa.teleporting", Map.of("time", countdown))));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Every second

        activeTasks.put(teleporteeUuid, task);
    }

    /**
     * Cancels an active teleport countdown for a player.
     */
    public void cancelTeleport(Player player) {
        cancelTeleport(player.getUniqueId());
    }

    /**
     * Cancels an active teleport countdown by UUID.
     */
    public void cancelTeleport(UUID playerUuid) {
        BukkitTask task = activeTasks.remove(playerUuid);
        if (task != null) {
            task.cancel();
        }
        cancelFlags.remove(playerUuid);
    }

    /**
     * Checks if a player has an active teleport in progress.
     */
    public boolean isTeleporting(UUID playerUuid) {
        return activeTasks.containsKey(playerUuid);
    }

    /**
     * Marks a player's teleport as cancelled due to damage.
     */
    public void cancelOnDamage(Player player) {
        if (isTeleporting(player.getUniqueId()) && config.isCancelOnDamage()) {
            cancelFlags.put(player.getUniqueId(), true);
            player.sendMessage(lang.format(player, "error.cancelled_damage"));
        }
    }

    /**
     * Marks a player's teleport as cancelled due to death.
     */
    public void cancelOnDeath(Player player) {
        if (isTeleporting(player.getUniqueId())) {
            cancelFlags.put(player.getUniqueId(), true);
            player.sendMessage(lang.format(player, "error.cancelled_death"));
        }
    }

    /**
     * Marks a player's teleport as cancelled due to quit.
     */
    public void cancelOnQuit(Player player) {
        cancelTeleport(player);
    }

    /**
     * Cancels all active teleports (on plugin disable).
     */
    public void cancelAll() {
        activeTasks.values().forEach(BukkitTask::cancel);
        activeTasks.clear();
        cancelFlags.clear();
    }

    // --- Helpers ---

    /**
     * Checks if a player has moved from their starting location.
     */
    private boolean hasMoved(Player player, Location start) {
        Location current = player.getLocation();
        return current.getBlockX() != start.getBlockX()
                || current.getBlockY() != start.getBlockY()
                || current.getBlockZ() != start.getBlockZ();
    }
}
