package pl.norbit.fastcommands.utils;

import pl.norbit.fastcommands.model.CommandAction;
import pl.norbit.fastcommands.model.WrapSettings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextWrapper {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\d+)}");

    private TextWrapper() {}

    public static List<String> wrap(CommandAction action, String[] args) {
        List<String> result = new ArrayList<>();

        if (action.getWrap() == null || !action.getWrap().isEnabled()) {
            return action.getAction();
        }

        WrapSettings wrap = action.getWrap();

        Map<Integer, Iterator<String>> iterators = new HashMap<>();

        for (Integer placeholder : wrap.getPlaceholders()) {
            String value = placeholder < args.length ? args[placeholder] : "";

            List<String> wrapped = wrap(value, wrap.getMaxLength());

            iterators.put(placeholder, wrapped.iterator());
        }

        for (String line : action.getAction()) {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(line);
            StringBuilder buffer = new StringBuilder();

            while (matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));

                Iterator<String> iterator = iterators.get(id);

                String replacement = "";

                if (iterator != null && iterator.hasNext()) {
                    replacement = iterator.next();
                }

                matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
            }

            matcher.appendTail(buffer);

            result.add(buffer.toString());
        }

        return result;
    }

    private static List<String> wrap(String text, int maxLength) {

        List<String> lines = new ArrayList<>();

        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }

        StringBuilder current = new StringBuilder();

        for (String word : text.split(" ")) {

            if (current.isEmpty()) {
                current.append(word);
                continue;
            }

            if (current.length() + 1 + word.length() <= maxLength) {
                current.append(" ").append(word);
            } else {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }
}
