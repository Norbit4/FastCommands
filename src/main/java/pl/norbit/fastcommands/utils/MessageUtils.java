package pl.norbit.fastcommands.utils;

import org.bukkit.command.CommandSender;
import pl.norbit.fastcommands.FastCommands;
import pl.norbit.fastcommands.placeholders.PlaceholderService;

public class MessageUtils {

    private MessageUtils() {}

    public static void toSender(CommandSender sender, String message, String... args) {
        sender.sendMessage(ChatUtils.format(PlaceholderService.replace(message, sender), args));
    }
    public static void toAll(CommandSender sender,String message, String... args) {
        FastCommands.getInstance().getServer().getOnlinePlayers().forEach(p ->
                p.sendMessage(ChatUtils.format(PlaceholderService.replace(message, sender), args)));
    }
}
