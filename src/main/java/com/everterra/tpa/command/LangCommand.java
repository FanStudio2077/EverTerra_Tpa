package com.everterra.tpa.command;

import com.everterra.tpa.EverTerraTPA;
import com.everterra.tpa.i18n.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * /lang <locale> — Change player language preference.
 */
public class LangCommand implements CommandExecutor, TabCompleter {

    private final LangManager lang;

    public LangCommand(EverTerraTPA plugin) {
        this.lang = plugin.getLangManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            // Show current language
            player.sendMessage(lang.format(player, "lang.current",
                    Map.of("locale", lang.getPlayerLocale(player))));
            return true;
        }

        String locale = args[0].toLowerCase();
        if (!lang.isLocaleSupported(locale)) {
            player.sendMessage(lang.format(player, "lang.invalid",
                    Map.of("locales", lang.getSupportedLocalesString())));
            return true;
        }

        lang.setPlayerLocale(player, locale);
        player.sendMessage(lang.format(player, "lang.changed",
                Map.of("locale", locale)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            for (String locale : lang.getSupportedLocales()) {
                if (locale.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(locale);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }
}
