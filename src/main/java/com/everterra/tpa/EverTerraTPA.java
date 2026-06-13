package com.everterra.tpa;

import com.everterra.tpa.command.*;
import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.core.CooldownManager;
import com.everterra.tpa.core.RequestManager;
import com.everterra.tpa.economy.EconomyManager;
import com.everterra.tpa.gui.GeyserDetector;
import com.everterra.tpa.gui.TpaGuiManager;
import com.everterra.tpa.i18n.LangManager;
import com.everterra.tpa.listener.PlayerListener;
import com.everterra.tpa.teleport.TeleportScheduler;
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
    private CooldownManager cooldownManager;
    private TeleportScheduler teleportScheduler;
    private EconomyManager economyManager;
    private TpaGuiManager guiManager;

    // --- Subsystems ---
    // Registered in later phases:
    // - EconomyManager
    // - TpaGuiManager

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
        getLogger().info("Initializing core systems...");
        this.requestManager = new RequestManager(this);
        this.cooldownManager = new CooldownManager(configManager);
        this.teleportScheduler = new TeleportScheduler(this);

        // 4. Setup economy
        getLogger().info("Setting up economy...");
        this.economyManager = new EconomyManager(configManager, getLogger());
        economyManager.setup();

        // 5. Initialize GUI
        getLogger().info("Initializing GUI system...");
        GeyserDetector.init();
        this.guiManager = new TpaGuiManager(this);

        // 6. Register commands
        registerCommands();

        // 7. Register listeners
        registerListeners();

        // 6. Start cleanup task
        startCleanupTask();

        long elapsed = System.currentTimeMillis() - startTime;
        getLogger().log(Level.INFO,
                "EverTerra-TPA v{0} enabled! ({1}ms)",
                new Object[]{getDescription().getVersion(), elapsed});
    }

    @Override
    public void onDisable() {
        if (teleportScheduler != null) {
            teleportScheduler.cancelAll();
        }
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
        Objects.requireNonNull(getCommand("lang"))
                .setExecutor(new LangCommand(this));
        Objects.requireNonNull(getCommand("tpareload"))
                .setExecutor(new TpareloadCommand(this));
        Objects.requireNonNull(getCommand("tpabypass"))
                .setExecutor(new TpabypassCommand(this));

        getLogger().info("Commands registered successfully.");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        getLogger().info("Event listeners registered successfully.");
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

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public TeleportScheduler getTeleportScheduler() {
        return teleportScheduler;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public TpaGuiManager getGuiManager() {
        return guiManager;
    }
}
