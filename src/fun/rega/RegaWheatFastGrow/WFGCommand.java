package fun.rega.RegaWheatFastGrow;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WFGCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("regawheatfastgrow.reload")) {
            sender.sendMessage(ChatColor.RED + "Чувак, у тебя нет прав на это!");
            return false;
        }

        Main.getInstance().reloadConfig();
        Main.getInstance().loadRegions();
        sender.sendMessage(ChatColor.GREEN + "Плагин успешно перезагружен!");
        return true;
    }
}
