package pl.norbit.fastcommands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.norbit.fastcommands.settings.Config;
import pl.norbit.fastcommands.utils.ChatUtils;


import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sendInfo(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            sender.sendMessage(ChatUtils.format(""));
            sender.sendMessage(ChatUtils.format("&7Reloading plugin..."));
            Config.load(true, sender);
            sender.sendMessage(ChatUtils.format(""));
            return true;
        }

        sendInfo(sender);
        return true;
    }

    private static void sendInfo(CommandSender sender){
        sender.sendMessage(ChatUtils.format(""));
        sender.sendMessage(ChatUtils.format("&7FastCommands by &aNorbit4!"));
        sender.sendMessage(ChatUtils.format("&7Website: &fhttps://github.com/Norbit4"));
        sender.sendMessage(ChatUtils.format(""));
        sender.sendMessage(ChatUtils.format("&7Type: &8/&bfc reload &7to reload plugin!"));
        sender.sendMessage(ChatUtils.format(""));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of("reload");
    }
}
