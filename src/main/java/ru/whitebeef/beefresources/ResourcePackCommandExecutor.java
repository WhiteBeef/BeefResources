package ru.whitebeef.beefresources;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ResourcePackCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bBeef&fResources&7] &cВы ввели недостаточно аргументов"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            BeefResources.getInstance().reload();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bBeef&fResources&7] &fКонфиг перезагружен"));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length <= 2 ? Arrays.asList(new String[]{"reload"}) : new ArrayList<>();
    }
}
