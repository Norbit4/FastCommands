package pl.norbit.fastcommands.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CommandNode {

    private String argumentId;
    private boolean argumentNode;
    private boolean tabPlayers;
    private boolean greedyArgument;

    private Map<String, CommandNode> subCommands = new HashMap<>();
    private List<CommandAction> actions = new ArrayList<>();
}