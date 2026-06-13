package com.everterra.tpa.gui;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.core.TpaRequest;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Central GUI dispatcher.
 * Routes to Java Inventory or Bedrock Form based on player platform.
 */
public class TpaGuiManager {

    private final EverTerraTPA plugin;
    private final LangManager lang;

    public TpaGuiManager(EverTerraTPA plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();
    }

    /**
     * Shows the receive-request GUI to a player (auto-detects platform).
     */
    public void showReceiveGui(Player player, TpaRequest request) {
        if (GeyserDetector.isBedrockPlayer(player)) {
            showBedrockReceiveGui(player, request);
        } else {
            showJavaReceiveGui(player, request);
        }
    }

    /**
     * Opens the send-request GUI for Bedrock players.
     */
    public void showBedrockSendGui(Player player) {
        if (!GeyserDetector.isBedrockPlayer(player)) return;

        BedrockTpaGui.openSendGui(
                player,
                lang.get(player, "bedrock_send.title"),
                lang.get(player, "bedrock_send.select_player"),
                lang.get(player, "bedrock_send.select_type_title"),
                lang.get(player, "bedrock_send.select_type_content"),
                lang.get(player, "bedrock_send.tpa_button"),
                lang.get(player, "bedrock_send.tpac_button"),
                lang.get(player, "bedrock_send.no_players")
        );
    }

    // --- Private helpers ---

    private void showJavaReceiveGui(Player player, TpaRequest request) {
        String title = lang.get(player, "gui.accept_title");
        String acceptName = lang.get(player, "gui.accept_item");
        List<String> acceptLore = lang.getRawList(player, "gui.accept_lore");
        String denyName = lang.get(player, "gui.deny_item");
        List<String> denyLore = lang.getRawList(player, "gui.deny_lore");
        String infoName = lang.get(player, "gui.info_item");
        List<String> infoLore = lang.getRawList(player, "gui.info_lore");

        JavaTpaGui.open(player, request, title,
                acceptName, acceptLore,
                denyName, denyLore,
                infoName, infoLore);
    }

    private void showBedrockReceiveGui(Player player, TpaRequest request) {
        BedrockTpaGui.openReceiveGui(
                player,
                request,
                lang.get(player, "bedrock.receive_title"),
                lang.get(player, "bedrock.receive_content"),
                lang.get(player, "bedrock.accept_button"),
                lang.get(player, "bedrock.deny_button")
        );
    }
}
