package com.everterra.tpa.gui;

import com.everterra.tpa.core.TpaRequest;
import com.everterra.tpa.core.TpaType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.ArrayList;
import java.util.List;

public final class BedrockTpaGui {

    private BedrockTpaGui() {}

    public static void openReceiveGui(Player player, TpaRequest request,
                                      String title, String content,
                                      String acceptBtn, String denyBtn) {
        if (!GeyserDetector.isFloodgateAvailable()) return;
        FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (fPlayer == null) return;

        Player senderPlayer = Bukkit.getPlayer(request.getSender());
        String senderName = senderPlayer != null ? senderPlayer.getName() : "Unknown";
        String typeText = request.getType() == TpaType.TPA
                ? "wants to teleport to you" : "invites you to teleport to them";
        String resolvedContent = content
                .replace("{player}", senderName)
                .replace("{type_text}", typeText);

        SimpleForm form = SimpleForm.builder()
                .title(title)
                .content(resolvedContent)
                .button(acceptBtn)
                .button(denyBtn)
                .responseHandler((String responseData) -> {
                    if (responseData == null || "null".equals(responseData)) return;
                    try {
                        int clicked = Integer.parseInt(responseData.trim());
                        Bukkit.getScheduler().runTask(
                                Bukkit.getPluginManager().getPlugin("EverTerra-TPA"),
                                () -> player.performCommand(clicked == 0 ? "tpaccept" : "tpadeny"));
                    } catch (NumberFormatException ignored) {}
                })
                .build();
        fPlayer.sendForm(form);
    }

    public static void openSendGui(Player player,
                                   String title, String selectPlayerText,
                                   String typeTitle, String typeContent,
                                   String tpaBtn, String tpacBtn,
                                   String noPlayersText) {
        if (!GeyserDetector.isFloodgateAvailable()) return;
        FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (fPlayer == null) return;

        List<String> names = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(player.getUniqueId())) names.add(p.getName());
        }
        if (names.isEmpty()) {
            SimpleForm form = SimpleForm.builder().title(title).content(noPlayersText).button("Close").build();
            fPlayer.sendForm(form);
            return;
        }
        SimpleForm.Builder builder = SimpleForm.builder().title(title).content(selectPlayerText);
        for (String name : names) builder.button(name);

        SimpleForm form = builder.responseHandler((String responseData) -> {
            if (responseData == null || "null".equals(responseData)) return;
            try {
                int idx = Integer.parseInt(responseData.trim());
                if (idx >= 0 && idx < names.size()) {
                    openTypeSelectGui(player, names.get(idx), typeTitle, typeContent, tpaBtn, tpacBtn);
                }
            } catch (NumberFormatException ignored) {}
        }).build();
        fPlayer.sendForm(form);
    }

    private static void openTypeSelectGui(Player player, String targetName,
                                          String title, String content,
                                          String tpaBtn, String tpacBtn) {
        if (!GeyserDetector.isFloodgateAvailable()) return;
        FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (fPlayer == null) return;

        String resolvedContent = content.replace("{player}", targetName);
        SimpleForm form = SimpleForm.builder()
                .title(title)
                .content(resolvedContent)
                .button(tpaBtn)
                .button(tpacBtn)
                .responseHandler((String responseData) -> {
                    if (responseData == null || "null".equals(responseData)) return;
                    try {
                        int clicked = Integer.parseInt(responseData.trim());
                        String cmd = (clicked == 0) ? "tpa " + targetName : "tpac " + targetName;
                        Bukkit.getScheduler().runTask(
                                Bukkit.getPluginManager().getPlugin("EverTerra-TPA"),
                                () -> player.performCommand(cmd));
                    } catch (NumberFormatException ignored) {}
                })
                .build();
        fPlayer.sendForm(form);
    }
}
