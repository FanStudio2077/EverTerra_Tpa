package com.everterra.tpa.economy;

import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.core.TpaType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wraps Vault economy API for TPA cost management.
 * Gracefully degrades if Vault is not installed.
 */
public class EconomyManager {

    private final ConfigManager config;
    private final Logger logger;
    private Economy economy;
    private boolean enabled;

    public EconomyManager(ConfigManager config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    /**
     * Attempts to hook into Vault economy.
     *
     * @return true if economy is available
     */
    public boolean setup() {
        if (!config.isEconomyEnabled()) {
            logger.info("Economy is disabled in config.");
            this.enabled = false;
            return false;
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            logger.warning("Vault not found! Economy features disabled.");
            this.enabled = false;
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null) {
            logger.warning("No economy provider found! Economy features disabled.");
            this.enabled = false;
            return false;
        }

        this.economy = rsp.getProvider();
        this.enabled = true;
        logger.log(Level.INFO, "Economy hooked: {0}", economy.getName());
        return true;
    }

    /**
     * Checks if economy is active and ready.
     */
    public boolean isEnabled() {
        return enabled && economy != null;
    }

    /**
     * Gets the cost for a given teleport type.
     */
    public double getCost(TpaType type) {
        return switch (type) {
            case TPA -> config.getTpaCost();
            case TPAC -> config.getTpacCost();
        };
    }

    /**
     * Gets a player's current balance.
     */
    public double getBalance(Player player) {
        if (!isEnabled()) return 0;
        return economy.getBalance(player);
    }

    /**
     * Checks if a player can afford the cost.
     */
    public boolean canAfford(Player player, TpaType type) {
        if (!isEnabled()) return true;
        if (player.hasPermission("tpa.bypass")) return true;
        return economy.has(player, getCost(type));
    }

    /**
     * Charges a player for a teleport type.
     *
     * @return true if charge was successful
     */
    public boolean charge(Player player, TpaType type) {
        if (!isEnabled()) return true;
        if (player.hasPermission("tpa.bypass")) return true;

        double cost = getCost(type);
        if (cost <= 0) return true;

        if (!economy.has(player, cost)) {
            return false;
        }

        economy.withdrawPlayer(player, cost);
        return true;
    }

    /**
     * Formats a monetary amount using the economy's currency format.
     */
    public String format(double amount) {
        if (!isEnabled()) return String.format("%.2f", amount);
        return economy.format(amount);
    }

    /**
     * Gets the underlying Vault Economy instance.
     */
    public Economy getEconomy() {
        return economy;
    }
}
