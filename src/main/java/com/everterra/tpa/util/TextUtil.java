package com.everterra.tpa.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

/**
 * Utility class for text formatting and color code processing.
 */
public final class TextUtil {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    private TextUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Translates alternate color codes (using '&') to ChatColor formatted string.
     */
    public static String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Converts a colorized string to Adventure Component.
     */
    public static Component toComponent(String text) {
        if (text == null) return Component.empty();
        return LEGACY_SERIALIZER.deserialize(colorize(text));
    }

    /**
     * Formats a message with prefix and color codes.
     */
    public static String format(String prefix, String key, String message) {
        return colorize(prefix + message);
    }

    /**
     * Strips all color codes from a string.
     */
    public static String stripColor(String text) {
        if (text == null) return "";
        return ChatColor.stripColor(colorize(text));
    }
}
