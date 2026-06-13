package com.everterra.tpa.core;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Manages all TPA requests in memory.
 * Thread-safe for async access where needed.
 */
public class RequestManager {

    private final EverTerraTPA plugin;
    private final ConfigManager config;

    // Active requests: key = target UUID (receiver), value = request
    private final Map<UUID, TpaRequest> pendingRequests = new ConcurrentHashMap<>();

    // Rate limiting: per-player request timestamps
    private final Map<UUID, Deque<Long>> requestTimestamps = new ConcurrentHashMap<>();

    public RequestManager(EverTerraTPA plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    // ==================== Request Lifecycle ====================

    /**
     * Creates a new TPA request from sender to target.
     *
     * @return null if successful, error message key if failed
     */
    public String createRequest(Player sender, Player target, TpaType type) {
        // Validate players
        if (sender == null || target == null) {
            return "error.player_not_found";
        }

        // Block self-TPA
        if (config.isBlockSelfTpa() && sender.getUniqueId().equals(target.getUniqueId())) {
            return "tpa.self_tpa";
        }

        // Rate limit check
        if (!checkRateLimit(sender)) {
            return "error.rate_limit";
        }

        // Check sender already has outgoing request (overwrite or deny)
        TpaRequest existingOutgoing = getOutgoingRequest(sender.getUniqueId());
        if (existingOutgoing != null) {
            if (config.isOverwriteOld()) {
                cancelRequest(sender); // Remove old
            } else {
                return "error.already_has_request";
            }
        }

        // Check target already has incoming request from someone else
        TpaRequest existingIncoming = getPendingRequest(target.getUniqueId());
        if (existingIncoming != null) {
            return "error.target_has_request";
        }

        // Create and store
        TpaRequest request = new TpaRequest(
                sender.getUniqueId(),
                target.getUniqueId(),
                type,
                config.getExpireTime()
        );
        pendingRequests.put(target.getUniqueId(), request);

        // Record rate limit timestamp
        recordRequest(sender);

        return null; // Success
    }

    /**
     * Accepts a pending request directed at the given player.
     *
     * @return the accepted request, or null if no pending request
     */
    public TpaRequest acceptRequest(Player acceptor) {
        TpaRequest request = pendingRequests.remove(acceptor.getUniqueId());
        if (request == null || request.isExpired()) {
            return null;
        }
        return request;
    }

    /**
     * Denies a pending request directed at the given player.
     *
     * @return the denied request, or null if no pending request
     */
    public TpaRequest denyRequest(Player denier) {
        return pendingRequests.remove(denier.getUniqueId());
    }

    /**
     * Cancels an outgoing request from the given player.
     *
     * @return the cancelled request, or null if no outgoing request
     */
    public TpaRequest cancelRequest(Player sender) {
        // Find and remove request where this player is the sender
        Iterator<Map.Entry<UUID, TpaRequest>> it = pendingRequests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, TpaRequest> entry = it.next();
            if (entry.getValue().getSender().equals(sender.getUniqueId())) {
                it.remove();
                return entry.getValue();
            }
        }
        return null;
    }

    // ==================== Queries ====================

    /**
     * Gets the pending request for a target player (incoming).
     */
    public TpaRequest getPendingRequest(UUID targetUuid) {
        TpaRequest request = pendingRequests.get(targetUuid);
        if (request != null && request.isExpired()) {
            pendingRequests.remove(targetUuid);
            return null;
        }
        return request;
    }

    /**
     * Gets the outgoing request from a sender.
     */
    public TpaRequest getOutgoingRequest(UUID senderUuid) {
        for (TpaRequest request : pendingRequests.values()) {
            if (request.getSender().equals(senderUuid)) {
                if (request.isExpired()) {
                    pendingRequests.remove(request.getTarget());
                    return null;
                }
                return request;
            }
        }
        return null;
    }

    /**
     * Checks if a player has any pending (incoming) request.
     */
    public boolean hasPendingRequest(UUID playerUuid) {
        return getPendingRequest(playerUuid) != null;
    }

    /**
     * Checks if a player has an active outgoing request.
     */
    public boolean hasOutgoingRequest(UUID playerUuid) {
        return getOutgoingRequest(playerUuid) != null;
    }

    // ==================== Rate Limiting ====================

    /**
     * Checks if the player is within their rate limit.
     */
    private boolean checkRateLimit(Player player) {
        if (player.hasPermission("tpa.bypass")) {
            return true;
        }

        int maxRequests = config.getMaxRequestsPerMinute();
        if (maxRequests <= 0) return true;

        Deque<Long> timestamps = requestTimestamps.get(player.getUniqueId());
        if (timestamps == null || timestamps.isEmpty()) {
            return true;
        }

        long oneMinuteAgo = System.currentTimeMillis() - 60_000L;
        // Clean old timestamps
        while (!timestamps.isEmpty() && timestamps.peekFirst() < oneMinuteAgo) {
            timestamps.pollFirst();
        }

        return timestamps.size() < maxRequests;
    }

    /**
     * Records a request timestamp for rate limiting.
     */
    private void recordRequest(Player player) {
        requestTimestamps
                .computeIfAbsent(player.getUniqueId(), k -> new ConcurrentLinkedDeque<>())
                .addLast(System.currentTimeMillis());
    }

    // ==================== Cleanup ====================

    /**
     * Removes all expired requests. Called periodically by cleanup task.
     */
    public int cleanupExpired() {
        int count = 0;
        Iterator<Map.Entry<UUID, TpaRequest>> it = pendingRequests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, TpaRequest> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    /**
     * Removes all requests involving a player (on quit).
     */
    public void removeAllForPlayer(UUID playerUuid) {
        // Remove incoming
        pendingRequests.remove(playerUuid);

        // Remove outgoing
        Iterator<Map.Entry<UUID, TpaRequest>> it = pendingRequests.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().getSender().equals(playerUuid)) {
                it.remove();
            }
        }

        // Clean rate limit data
        requestTimestamps.remove(playerUuid);
    }

    /**
     * Gets the count of active requests (for debugging).
     */
    public int getActiveRequestCount() {
        cleanupExpired();
        return pendingRequests.size();
    }
}
