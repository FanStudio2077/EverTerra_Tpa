package com.everterra.tpa.core;

import com.everterra.tpa.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player cooldowns for TPA and TPAC commands.
 * Thread-safe.
 */
public class CooldownManager {

    private final ConfigManager config;

    // Map of player UUID -> cooldown end timestamp (millis)
    private final Map<UUID, Long> tpaCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> tpacCooldowns = new ConcurrentHashMap<>();

    public CooldownManager(ConfigManager config) {
        this.config = config;
    }

    /**
     * Records a cooldown for the given player and type.
     */
    public void setCooldown(Player player, TpaType type) {
        long durationMs = getDuration(type) * 1000L;
        long endTime = System.currentTimeMillis() + durationMs;
        getMap(type).put(player.getUniqueId(), endTime);
    }

    /**
     * Checks if a player is currently on cooldown for the given type.
     *
     * @return remaining seconds, or 0 if not on cooldown
     */
    public long getRemaining(Player player, TpaType type) {
        if (player.hasPermission("tpa.bypass")) {
            return 0;
        }

        Long endTime = getMap(type).get(player.getUniqueId());
        if (endTime == null) return 0;

        long remaining = (endTime - System.currentTimeMillis()) / 1000L;
        if (remaining <= 0) {
            getMap(type).remove(player.getUniqueId());
            return 0;
        }
        return remaining;
    }

    /**
     * Checks if a player is on cooldown.
     */
    public boolean isOnCooldown(Player player, TpaType type) {
        return getRemaining(player, type) > 0;
    }

    /**
     * Clears all cooldowns for a player.
     */
    public void clearAll(Player player) {
        UUID uuid = player.getUniqueId();
        tpaCooldowns.remove(uuid);
        tpacCooldowns.remove(uuid);
    }

    /**
     * Clears a specific cooldown for a player.
     */
    public void clearCooldown(Player player, TpaType type) {
        getMap(type).remove(player.getUniqueId());
    }

    // --- Helpers ---

    private long getDuration(TpaType type) {
        return switch (type) {
            case TPA -> config.getCooldownTpa();
            case TPAC -> config.getCooldownTpac();
        };
    }

    private Map<UUID, Long> getMap(TpaType type) {
        return switch (type) {
            case TPA -> tpaCooldowns;
            case TPAC -> tpacCooldowns;
        };
    }
}
