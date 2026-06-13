package com.everterra.tpa;

import com.everterra.tpa.command.*;
import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.core.RequestManager;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

/**
 * EverTerra-TPA — Cross-platform TPA system for PaperMC.
 * <p>
 * Main plugin class. Handles lifecycle (onEnable / onDisable)
 * and initializes all subsystems.
 */
public final class EverTerraTPA extends JavaPlugin {

    private static EverTerraTPA instance;

    private ConfigManager configManager;
    private LangManager langManager;
    private RequestManager requestManager;

    // --- Subsystems ---
    // Registered in later phases:
    // - CooldownManager
    // - EconomyManager
    // - TpaGuiManager
    // - TeleportScheduler

    @Override
    public void onEnable() {
        instance = this;

        long startTime = System.currentTimeMillis();

        // 1. Load configuration
        getLogger().info("Loading configuration...");
        this.configManager = new ConfigManager(this);
        configManager.load();

        // 2. Load I18N
        getLogger().info("Loading language files...");
        this.langManager = new LangManager(this, configManager);
        langManager.load();

        // 3. Initialize core systems
        getLogger().info("Initializing request manager...");
        this.requestManager = new RequestManager(this);

        // 4. Register commands
        registerCommands();

        // 5. Register listeners (Phase 7)
        // registerListeners();

        // 6. Start cleanup task
        startCleanupTask();

        long elapsed = System.currentTimeMillis() - startTime;
        getLogger().log(Level.INFO,
                "EverTerra-TPA v{0} enabled! ({1}ms)",
                new Object[]{getDescription().getVersion(), elapsed});
    }

    @Override
    public void onDisable() {
        getLogger().info("EverTerra-TPA disabled. Goodbye!");
        instance = null;
    }

    // ==================== Command Registration ====================

    private void registerCommands() {
        Objects.requireNonNull(getCommand("tpa"))
                .setExecutor(new TpaCommand(this, requestManager));
        Objects.requireNonNull(getCommand("tpac"))
                .setExecutor(new TpacCommand(this, requestManager));
        Objects.requireNonNull(getCommand("tpaccept"))
                .setExecutor(new TpacceptCommand(this, requestManager));
        Objects.requireNonNull(getCommand("tpadeny"))
                .setExecutor(new TpadenyCommand(this, requestManager));
        Objects.requireNonNull(getCommand("tpacancel"))
                .setExecutor(new TpacancelCommand(this, requestManager));

        getLogger().info("Commands registered successfully.");
    }

    // ==================== Cleanup Task ====================

    private void startCleanupTask() {
        long interval = configManager.getCleanupInterval() * 20L; // Convert seconds to ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            int cleaned = requestManager.cleanupExpired();
            if (cleaned > 0) {
                getLogger().log(Level.FINE, "Cleaned up {0} expired requests.", cleaned);
            }
        }, interval, interval);
        getLogger().log(Level.INFO, "Cleanup task started (interval: {0}s).",
                configManager.getCleanupInterval());
    }

    // ==================== Static Accessor ====================

    public static EverTerraTPA getInstance() {
        return instance;
    }

    // ==================== Subsystem Getters ====================

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }
}
