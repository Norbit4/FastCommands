package pl.norbit.fastcommands.placeholders;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.norbit.fastcommands.settings.Config;

public class PlaceholderService {
    private PlaceholderService() {}

    public static String replace(String message, CommandSender commandSender) {
        if(!(commandSender instanceof Player p)){
            return PlaceholderUtil.format(message, null);
        }

        if(Config.isPapiEnable()){
            message = PlaceholderUtil.format(message, p);
        }

        return message.replace("{player}", p.getName());
    }
}
