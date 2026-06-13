package com.everterra.tpa.i18n;

import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * I18N Language Manager.
 * Handles loading locale files and resolving messages per player.
 */
public class LangManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    private final Map<String, YamlConfiguration> locales = new HashMap<>();
    private final Map<UUID, String> playerLocales = new ConcurrentHashMap<>();

    private static final String LANG_DIR = "lang";
    private static final List<String> SUPPORTED_LOCALES = List.of("zh_CN", "en_US");

    public LangManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Loads all language files from the lang/ directory.
     * Saves default locale files if they don't exist.
     */
    public void load() {
        locales.clear();

        // Ensure lang directory exists
        File langDir = new File(plugin.getDataFolder(), LANG_DIR);
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        for (String locale : SUPPORTED_LOCALES) {
            // Save default from resources if not present
            String fileName = locale + ".yml";
            File langFile = new File(langDir, fileName);
            if (!langFile.exists()) {
                plugin.saveResource(LANG_DIR + "/" + fileName, false);
            }

            // Load the locale file
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(langFile);

            // Also load the default from jar to merge missing keys
            InputStream defaultStream = plugin.getResource(LANG_DIR + "/" + fileName);
            if (defaultStream != null) {
                YamlConfiguration defaultYaml = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
                yaml.setDefaults(defaultYaml);
            }

            locales.put(locale, yaml);
            plugin.getLogger().log(Level.INFO, "Loaded language: {0}", locale);
        }

        plugin.getLogger().log(Level.INFO, "I18N system loaded with {0} languages.", locales.size());
    }

    /**
     * Gets a localized message for a player.
     * Resolves placeholders like {player}, {time}, etc.
     */
    public String get(Player player, String key) {
        String locale = getPlayerLocale(player);
        return getRaw(locale, key);
    }

    /**
     * Gets a localized message with placeholder replacements.
     */
    public String get(Player player, String key, Map<String, Object> placeholders) {
        String message = get(player, key);
        if (message != null && placeholders != null) {
            for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}",
                        String.valueOf(entry.getValue()));
            }
        }
        return message;
    }

    /**
     * Gets the prefix for a player's locale.
     */
    public String getPrefix(Player player) {
        String locale = getPlayerLocale(player);
        YamlConfiguration yaml = locales.get(locale);
        if (yaml == null) yaml = locales.get(configManager.getFallbackLocale());
        if (yaml == null) return "";
        return TextUtil.colorize(yaml.getString("prefix", ""));
    }

    /**
     * Formats a full message with prefix: prefix + message.
     */
    public String format(Player player, String key) {
        return getPrefix(player) + get(player, key);
    }

    /**
     * Formats a full message with prefix and placeholders.
     */
    public String format(Player player, String key, Map<String, Object> placeholders) {
        return getPrefix(player) + get(player, key, placeholders);
    }

    /**
     * Gets the raw message string from a locale file (without prefix or colorizing).
     */
    private String getRaw(String locale, String key) {
        YamlConfiguration yaml = locales.get(locale);
        String message = null;

        if (yaml != null) {
            message = yaml.getString(key);
        }

        // Fallback
        if (message == null) {
            String fallback = configManager.getFallbackLocale();
            if (!fallback.equals(locale)) {
                YamlConfiguration fallbackYaml = locales.get(fallback);
                if (fallbackYaml != null) {
                    message = fallbackYaml.getString(key);
                }
            }
        }

        return message != null ? TextUtil.colorize(message) : "&cMissing: " + key;
    }

    /**
     * Gets a list of strings from a locale key.
     */
    public List<String> getRawList(Player player, String key) {
        String locale = getPlayerLocale(player);
        YamlConfiguration yaml = locales.get(locale);
        List<String> list = null;

        if (yaml != null) {
            list = yaml.getStringList(key);
        }

        if ((list == null || list.isEmpty()) && !locale.equals(configManager.getFallbackLocale())) {
            YamlConfiguration fallbackYaml = locales.get(configManager.getFallbackLocale());
            if (fallbackYaml != null) {
                list = fallbackYaml.getStringList(key);
            }
        }

        if (list == null) return List.of("&cMissing: " + key);

        return list.stream().map(TextUtil::colorize).toList();
    }

    /**
     * Gets the player's preferred locale.
     */
    public String getPlayerLocale(Player player) {
        if (configManager.isPlayerLanguage()) {
            String locale = playerLocales.get(player.getUniqueId());
            if (locale != null && locales.containsKey(locale)) {
                return locale;
            }
        }
        return configManager.getDefaultLocale();
    }

    /**
     * Sets a player's locale preference.
     */
    public void setPlayerLocale(Player player, String locale) {
        if (SUPPORTED_LOCALES.contains(locale)) {
            playerLocales.put(player.getUniqueId(), locale);
        }
    }

    /**
     * Removes a player's locale preference (reverts to default).
     */
    public void removePlayerLocale(Player player) {
        playerLocales.remove(player.getUniqueId());
    }

    /**
     * Gets the list of supported locale codes.
     */
    public List<String> getSupportedLocales() {
        return SUPPORTED_LOCALES;
    }

    /**
     * Checks if a locale code is supported.
     */
    public boolean isLocaleSupported(String locale) {
        return SUPPORTED_LOCALES.contains(locale);
    }

    /**
     * Gets a display-friendly string of supported locales.
     */
    public String getSupportedLocalesString() {
        return String.join(", ", SUPPORTED_LOCALES);
    }
}
