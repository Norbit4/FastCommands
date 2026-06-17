package pl.norbit.fastcommands.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WrapSettings {

    private boolean enabled;
    private int maxLength = 35;
    private List<Integer> placeholders = new ArrayList<>();
}
