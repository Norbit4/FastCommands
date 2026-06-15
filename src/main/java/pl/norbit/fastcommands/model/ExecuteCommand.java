package pl.norbit.fastcommands.model;

import lombok.*;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import pl.norbit.fastcommands.FastCommands;
import pl.norbit.fastcommands.cooldown.CooldownService;
import pl.norbit.fastcommands.placeholders.PlaceholderService;
import pl.norbit.fastcommands.settings.Config;
import pl.norbit.fastcommands.utils.ChatUtils;
import pl.norbit.fastcommands.utils.MessageUtils;
import pl.norbit.fastcommands.utils.PermissionUtils;

import java.util.*;

@Getter
@Setter
public class ExecuteCommand extends BukkitCommand {
    private static Server server = FastCommands.getInstance().getServer();
    private static final HashMap<String, ExecuteCommand> COMMANDS = new HashMap<>();
    private static final CooldownService cooldownService = new CooldownService();

    private String cmdName;
    private List<CommandAction> actions;
    private String perm;
    private String permMessage;
    private String cooldownMessage;
    private int cooldown;

    private String argsMessage;

    private boolean completer;
    private Map<String, CommandNode> subCommands;

    public ExecuteCommand(@NotNull String name) {
        super(name);
        this.actions = new ArrayList<>();
        this.cmdName = name;
        this.subCommands = new HashMap<>();
    }

    private CommandNode findArgumentNode(Map<String, CommandNode> map) {
        return map.values()
                .stream()
                .filter(CommandNode::isArgumentNode)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!PermissionUtils.hasPermission(perm, sender)){
            if(permMessage != null && !permMessage.isEmpty()) MessageUtils.toSender(sender, permMessage);
            else MessageUtils.toSender(sender, Config.getDefaultPermissionMessage());
            return true;
        }

        if (cooldown > 0 && cooldownService.isOnCooldown(sender, cmdName)) {
            long remaining = cooldownService.getRemainingSeconds(sender, cmdName);

            if (remaining <= 0) {
                remaining = 1;
            }

            MessageUtils.toSender(
                    sender,
                    cooldownMessage.replace("{time}", String.valueOf(remaining))
            );

            return true;
        }

        //execute default actions when no args
        if(args.length < 1){
            actions.forEach(action -> executeAction(action, sender, args));
            applyCooldown(sender);
            return true;
        }
        CommandNode currentNode = null;
        Map<String, CommandNode> current = subCommands;

        String[] parsedArgs = args;

        for(int i = 0; i < args.length; i++) {
            String arg = args[i];

            CommandNode next = current.get(arg.toLowerCase());

            if(next == null) {
                next = findArgumentNode(current);

                if(next == null) {
                    break;
                }

                if(next.isGreedyArgument()) {
                    List<String> newArgs = new ArrayList<>(
                            Arrays.asList(args).subList(0, i)
                    );

                    String remaining = String.join(
                            " ",
                            Arrays.copyOfRange(args, i, args.length)
                    );

                    newArgs.add(remaining);

                    parsedArgs = newArgs.toArray(new String[0]);

                    currentNode = next;
                    break;
                }
            }

            currentNode = next;
            current = next.getSubCommands();
        }

        if(currentNode != null && !currentNode.getActions().isEmpty()){
            for (CommandAction action : currentNode.getActions()) {
                executeAction(action, sender, parsedArgs);
            }
            applyCooldown(sender);
            return true;
        }

        //fallback to root actions
        for (CommandAction action : actions) {
            executeAction(action, sender, parsedArgs);
        }

        applyCooldown(sender);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(
            @NotNull CommandSender sender,
            @NotNull String alias,
            @NotNull String[] args) {

        if(!completer){
            return Collections.emptyList();
        }

        Map<String, CommandNode> current = subCommands;

        for(int i = 0; i < args.length - 1; i++){
            CommandNode next =
                    current.get(args[i].toLowerCase());

            if(next == null){
                next = findArgumentNode(current);

                if(next == null){
                    return Collections.emptyList();
                }
            }

            current = next.getSubCommands();
        }

        String lastArg =
                args.length == 0
                        ? ""
                        : args[args.length - 1].toLowerCase();

        List<String> suggestions = new ArrayList<>();

        current.forEach((key, node) -> {
            if(node.isArgumentNode()){
                if(node.isTabPlayers()){
                    server.getOnlinePlayers()
                            .forEach(player -> {
                                if(player.getName()
                                        .toLowerCase()
                                        .startsWith(lastArg)) {

                                    suggestions.add(player.getName());
                                }
                            });
                }
            }else{
                if(key.startsWith(lastArg)){
                    suggestions.add(key);
                }
            }
        });

        return suggestions;
    }

    private void applyCooldown(CommandSender sender) {
        if (cooldown > 0) {
            cooldownService.setCooldown(sender, cmdName, cooldown);
        }
    }

    public void register(){
        server.getCommandMap().register("", this);
        COMMANDS.put(cmdName, this);
    }

    public static void updateCommands(List<ExecuteCommand> updatedCommands, CommandSender sender){
        boolean reloadWarn = false;
        MessageUtils.toSender(sender, "");

        for (ExecuteCommand cmd : updatedCommands) {
            var executeCommand = COMMANDS.get(cmd.getCmdName());

            if(executeCommand == null){
                MessageUtils.toSender(sender,
                        "&cCommand &e" + cmd.getCmdName() + " &cwas not previously registered!");
                reloadWarn = true;
                continue;
            }else{
                MessageUtils.toSender(sender,
                        "&7Command &a" + cmd.getCmdName() + " &7has been reloaded!");
            }

            executeCommand.setPerm(cmd.getPerm());
            executeCommand.setCompleter(cmd.isCompleter());
            executeCommand.setPermMessage(cmd.getPermMessage());
            executeCommand.setSubCommands(cmd.getSubCommands());
            executeCommand.setArgsMessage(cmd.getArgsMessage());
            executeCommand.setCooldownMessage(cmd.getCooldownMessage());
            executeCommand.setActions(cmd.getActions());
            executeCommand.setCooldown(cmd.getCooldown());
        }
        MessageUtils.toSender(sender, "");
        if(reloadWarn) {
            MessageUtils.toSender(sender,
                    "&cUnable to reload change detected!");
            MessageUtils.toSender(sender,
                    "&cTo reload changing the name of a command or adding a new command you must reload the server!");
        }else{
            MessageUtils.toSender(sender, "&aAll commands have been successfully reloaded!");
        }
    }

    private void executeAction(CommandAction cmdAction, CommandSender sender, @NotNull String[] args){
        List<String> cmdActions = cmdAction.getAction();

        if(cmdActions.isEmpty()) return;

        int delay = cmdAction.getDelay();

        if(delay == 0){
            execute(cmdAction, sender, args);
        }else {
            FastCommands instance = FastCommands.getInstance();
            server.getScheduler()
                    .runTaskLater(instance, () -> execute(cmdAction, sender, args), delay);
        }
    }

    private void execute(CommandAction cmdAction, CommandSender sender, @NotNull String[] args){
        List<String> cmdActions = cmdAction.getAction();
        switch (cmdAction.getType()) {
            case REPLACE -> {
                var usageCommand = new StringBuilder(cmdActions.get(0));

                Arrays.stream(args).forEach(arg -> usageCommand.append(" ").append(arg));

                server.dispatchCommand(sender, usageCommand.toString());
            }
            case TEXT -> cmdActions.forEach(m -> MessageUtils.toSender(sender, m, args));
            case PLAYER_COMMAND ->
                    cmdActions.forEach(c -> {
                        String command = ChatUtils.replace(PlaceholderService.replace(c, sender), args);
                        server.dispatchCommand(sender, command);
                    });
            case SERVER_COMMAND ->
                    cmdActions.forEach(c -> {
                        String command = ChatUtils.replace(PlaceholderService.replace(c, sender), args);
                        server.dispatchCommand(server.getConsoleSender(), command);
                    });
            case BROADCAST -> cmdActions.forEach(m -> MessageUtils.toAll(sender, m, args));
        }
    }
}
