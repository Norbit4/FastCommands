package pl.norbit.fastcommands.settings;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.norbit.fastcommands.FastCommands;
import pl.norbit.fastcommands.model.ExecuteCommand;

import java.util.List;

public class Config {
    @Getter
    @Setter
    private static boolean papiEnable;
    @Getter
    private static String defaultPermissionMessage;
    @Getter
    private static String defaultCooldownMessage;

    private Config() {}

    public static void load(boolean reload, CommandSender sender){
        var instance = FastCommands.getInstance();

        if(!reload) instance.saveDefaultConfig();
        else instance.reloadConfig();

        FileConfiguration config = instance.getConfig();

        ConfigurationSection commandsSection = config.getConfigurationSection("commands");

        if(commandsSection == null){
            return;
        }

        defaultCooldownMessage = config.getString("default-message.cooldown");
        defaultPermissionMessage = config.getString("default-message.permission");

        List<ExecuteCommand> commands = ConfigUtils.getCommands(commandsSection);

        if(reload) {
            ExecuteCommand.updateCommands(commands, sender);
        }
        else{
            commands.forEach(ExecuteCommand::register);
        }
    }
}
