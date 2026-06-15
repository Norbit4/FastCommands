package pl.norbit.fastcommands.settings;

import org.bukkit.configuration.ConfigurationSection;
import pl.norbit.fastcommands.model.ArgType;
import pl.norbit.fastcommands.model.CommandAction;
import pl.norbit.fastcommands.model.CommandNode;
import pl.norbit.fastcommands.model.ExecuteCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigUtils {
    private ConfigUtils() {}

    private static void parseRoute(
            String route,
            ConfigurationSection routeSec,
            ExecuteCommand command
    ) {

        String[] parts = route.split(" ");

        Map<String, CommandNode> currentMap = command.getSubCommands();
        CommandNode currentNode = null;

        for (String part : parts) {
            boolean argument = part.startsWith("<") && part.endsWith(">")
                    || (part.startsWith("[") && part.endsWith("]"));

            boolean greedy = part.startsWith("[") && part.endsWith("]");

            String key = argument
                    ? "__arg__" + part.substring(1, part.length() - 1)
                    : part.toLowerCase();

            CommandNode next = currentMap.get(key);

            if (next == null) {
                next = new CommandNode();

                if (argument) {
                    String argName = part.substring(1, part.length() - 1);

                    next.setArgumentNode(true);
                    next.setArgumentId(argName);
                    next.setGreedyArgument(greedy);

                    if (argName.equalsIgnoreCase("player")
                            || argName.equalsIgnoreCase("target")
                            || argName.equalsIgnoreCase("player-name")) {

                        next.setTabPlayers(true);
                    }
                }

                currentMap.put(key, next);
            }
            currentNode = next;
            currentMap = next.getSubCommands();
        }

        if (currentNode != null) {
            ConfigurationSection actions = routeSec.getConfigurationSection("actions");

            if (actions != null) {
                currentNode.setActions(getActions(actions));
            }
        }
    }

    public static List<ExecuteCommand> getCommands(ConfigurationSection sec) {
        List<ExecuteCommand> commands = new ArrayList<>();

        sec.getKeys(false).forEach(name -> {
            ConfigurationSection section = sec.getConfigurationSection(name);

            if(section == null) return;

            ExecuteCommand executeCommand = new ExecuteCommand(name);

            boolean completer = section.getBoolean("completer");
            String perm = section.getString("permission");
            String permMessage = section.getString("permission-message", Config.getDefaultPermissionMessage());

            String cooldownMessage = section.getString("cooldown-message", Config.getDefaultCooldownMessage());
            int cooldown = section.getInt("cooldown");

            executeCommand.setPerm(perm);
            executeCommand.setCooldownMessage(cooldownMessage);
            executeCommand.setCompleter(completer);
            executeCommand.setPermMessage(permMessage);
            executeCommand.setCooldown(cooldown);

            ConfigurationSection actionsSection = section.getConfigurationSection("actions");

            //routes
            ConfigurationSection routes = section.getConfigurationSection("routes");

            if(routes != null){
                routes.getKeys(false).forEach(route -> {
                    ConfigurationSection routeSec = routes.getConfigurationSection(route);

                    if(routeSec == null) return;

                    parseRoute(route, routeSec, executeCommand);
                });
            }

            //default actions
            if(actionsSection != null){
                executeCommand.setActions(getActions(actionsSection));
            }

            commands.add(executeCommand);
        });
        return commands;
    }

    private static List<CommandAction> getActions(ConfigurationSection section){
        List<CommandAction> actions = new ArrayList<>();

        section.getKeys(false).forEach(name -> {
            ConfigurationSection actionSection = section.getConfigurationSection(name);

            if(actionSection == null) return;

            String type = actionSection.getString("type");
            int delay = actionSection.getInt("delay");

            if(type == null) return;

            ArgType commandType;
            try {
                commandType = ArgType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return;
            }

            CommandAction action = new CommandAction();

            action.setType(commandType);
            action.setDelay(delay);
            action.setAction(actionSection.getStringList("action"));

            actions.add(action);
        });

        return actions;
    }
}
