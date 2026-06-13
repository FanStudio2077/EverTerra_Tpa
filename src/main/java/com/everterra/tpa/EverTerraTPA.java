package com.everterra.tpa;

import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.plugin.java.JavaPlugin;

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

    // --- Subsystems ---
    // Registered in later phases:
    // - RequestManager
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

        // 3. Register commands & listeners (Phase 3+)
        // registerCommands();
        // registerListeners();

        // 4. Start cleanup task (Phase 3+)
        // startCleanupTask();

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

    // --- Static Accessor ---

    public static EverTerraTPA getInstance() {
        return instance;
    }

    // --- Subsystem Getters ---

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }
}
