package com.everterra.tpa.gui;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

/**
 * Detects whether a player is using Bedrock Edition (via Geyser).
 * Uses Floodgate API as the primary detection method,
 * with Bukkit-based fallback.
 */
public final class GeyserDetector {

    private static boolean floodgateAvailable = false;

    private GeyserDetector() {}

    /**
     * Initializes the detector. Call once on plugin enable.
     */
    public static void init() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgateAvailable = true;
        } catch (ClassNotFoundException e) {
            floodgateAvailable = false;
        }
    }

    /**
     * Checks if a player is using Bedrock Edition.
     */
    public static boolean isBedrockPlayer(Player player) {
        // Primary: Floodgate detection
        if (floodgateAvailable) {
            try {
                return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
            } catch (Exception ignored) {
                // Fall through to fallback
            }
        }

        // Fallback: Check for Geyser-spoofed properties
        // Geyser players often have a specific client brand or empty hostname
        String hostname = player.getAddress() != null
                ? player.getAddress().getHostName() : "";
        return hostname.startsWith("Geyser_")
                || (player.getClientBrandName() != null
                    && player.getClientBrandName().contains("Geyser"));
    }

    /**
     * Returns whether Floodgate API is available.
     */
    public static boolean isFloodgateAvailable() {
        return floodgateAvailable;
    }
}
