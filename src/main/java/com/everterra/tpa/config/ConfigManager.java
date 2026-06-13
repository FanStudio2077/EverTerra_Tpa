package com.everterra.tpa.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Manages the plugin's main configuration (config.yml).
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    // --- Teleport ---
    private int teleportDelay;
    private boolean cancelOnMove;
    private boolean cancelOnDamage;
    private boolean cancelOnWorldChange;

    // --- Economy ---
    private boolean economyEnabled;
    private double tpaCost;
    private double tpacCost;

    // --- Cooldown ---
    private int cooldownTpa;
    private int cooldownTpac;

    // --- Request ---
    private int expireTime;
    private int maxRequestsPerMinute;
    private boolean overwriteOld;
    private boolean blockSelfTpa;

    // --- I18N ---
    private String defaultLocale;
    private String fallbackLocale;
    private boolean playerLanguage;

    // --- Cleanup ---
    private int cleanupInterval;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads config.yml from disk. Creates default if missing.
     */
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        // Teleport
        teleportDelay = config.getInt("teleport.delay", 5);
        cancelOnMove = config.getBoolean("teleport.cancel_on_move", true);
        cancelOnDamage = config.getBoolean("teleport.cancel_on_damage", true);
        cancelOnWorldChange = config.getBoolean("teleport.cancel_on_world_change", false);

        // Economy
        economyEnabled = config.getBoolean("economy.enabled", true);
        tpaCost = config.getDouble("economy.tpa_cost", 50.0);
        tpacCost = config.getDouble("economy.tpac_cost", 80.0);

        // Cooldown
        cooldownTpa = config.getInt("cooldown.tpa", 60);
        cooldownTpac = config.getInt("cooldown.tpac", 120);

        // Request
        expireTime = config.getInt("request.expire_time", 30);
        maxRequestsPerMinute = config.getInt("request.max_per_minute", 3);
        overwriteOld = config.getBoolean("request.overwrite_old", true);
        blockSelfTpa = config.getBoolean("request.block_self_tpa", true);

        // I18N
        defaultLocale = config.getString("i18n.default", "zh_CN");
        fallbackLocale = config.getString("i18n.fallback", "en_US");
        playerLanguage = config.getBoolean("i18n.player_language", true);

        // Cleanup
        cleanupInterval = config.getInt("cleanup.interval", 20);

        plugin.getLogger().log(Level.INFO, "Configuration loaded successfully.");
    }

    /**
     * Reloads configuration from disk.
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        load();
        plugin.getLogger().log(Level.INFO, "Configuration reloaded.");
    }

    // --- Getters ---

    public int getTeleportDelay() { return teleportDelay; }
    public boolean isCancelOnMove() { return cancelOnMove; }
    public boolean isCancelOnDamage() { return cancelOnDamage; }
    public boolean isCancelOnWorldChange() { return cancelOnWorldChange; }

    public boolean isEconomyEnabled() { return economyEnabled; }
    public double getTpaCost() { return tpaCost; }
    public double getTpacCost() { return tpacCost; }

    public int getCooldownTpa() { return cooldownTpa; }
    public int getCooldownTpac() { return cooldownTpac; }

    public int getExpireTime() { return expireTime; }
    public int getMaxRequestsPerMinute() { return maxRequestsPerMinute; }
    public boolean isOverwriteOld() { return overwriteOld; }
    public boolean isBlockSelfTpa() { return blockSelfTpa; }

    public String getDefaultLocale() { return defaultLocale; }
    public String getFallbackLocale() { return fallbackLocale; }
    public boolean isPlayerLanguage() { return playerLanguage; }

    public int getCleanupInterval() { return cleanupInterval; }
}
