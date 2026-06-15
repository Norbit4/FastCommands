package pl.norbit.fastcommands.model;

import lombok.Data;

import java.util.List;

@Data
public class CommandAction {
    private ArgType type;
    private List<String> action;
    private int delay;
}
