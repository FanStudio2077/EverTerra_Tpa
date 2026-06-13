package com.everterra.tpa.gui;

import com.everterra.tpa.core.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Java Edition Inventory GUI for receiving TPA requests.
 * Shows accept/deny buttons with countdown info.
 */
public final class JavaTpaGui {

    private static final int GUI_SIZE = 9;
    private static final int ACCEPT_SLOT = 2;
    private static final int INFO_SLOT = 4;
    private static final int DENY_SLOT = 6;

    private JavaTpaGui() {}

    /**
     * Opens the TPA request GUI for a Java player.
     */
    public static void open(Player player, TpaRequest request, String acceptTitle,
                            String acceptName, List<String> acceptLore,
                            String denyName, List<String> denyLore,
                            String infoName, List<String> infoLore) {

        Player sender = Bukkit.getPlayer(request.getSender());
        String senderName = sender != null ? sender.getName() : "Unknown";

        String typeText = request.getType().name();
        String timeStr = String.valueOf(request.getRemainingSeconds());

        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, acceptTitle);

        // Accept button (green wool)
        gui.setItem(ACCEPT_SLOT, createButton(Material.LIME_WOOL, acceptName, acceptLore));

        // Info item (clock)
        List<String> resolvedInfoLore = new ArrayList<>();
        for (String line : infoLore) {
            resolvedInfoLore.add(line
                    .replace("{sender}", senderName)
                    .replace("{type}", typeText)
                    .replace("{time}", timeStr));
        }
        gui.setItem(INFO_SLOT, createButton(Material.CLOCK, infoName, resolvedInfoLore));

        // Deny button (red wool)
        gui.setItem(DENY_SLOT, createButton(Material.RED_WOOL, denyName, denyLore));

        player.openInventory(gui);
    }

    /**
     * Creates an inventory button item.
     */
    private static ItemStack createButton(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static int getAcceptSlot() { return ACCEPT_SLOT; }
    public static int getDenySlot() { return DENY_SLOT; }
}
