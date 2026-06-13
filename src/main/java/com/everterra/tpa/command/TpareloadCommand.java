package com.everterra.tpa.command;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.config.ConfigManager;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /tpareload — Reload plugin configuration (admin only).
 */
public class TpareloadCommand implements CommandExecutor {

    private final EverTerraTPA plugin;
    private final ConfigManager config;
    private final LangManager lang;

    public TpareloadCommand(EverTerraTPA plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.lang = plugin.getLangManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tpa.reload")) {
            sender.sendMessage("You don't have permission.");
            return true;
        }

        config.reload();
        lang.load();

        // Send reload message (no player context, use default locale)
        sender.sendMessage(lang.get(null, "admin.reloaded"));
        return true;
    }
}
