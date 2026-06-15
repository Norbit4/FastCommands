package pl.norbit.fastcommands.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {
    public static final String CODES = "((?<=%1$s)|(?=%1$s))";

    private ChatUtils() {}

    public static String format(String input, String... args){
        return format(replace(input, args));
    }

    public static String replace(String input, String[] args){
        for (int i = 0; i < args.length; i++) {
            input = input.replace("{" + i + "}", args[i]);
        }
        return input;
    }

    public static String format(String text){
        String[] texts = text.split(String.format(CODES, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++){
            if (texts[i].equalsIgnoreCase("&")){
                i++;
                if (texts[i].charAt(0) == '#'){
                    finalText.append(ChatColor.of(texts[i].substring(0, 7)) + texts[i].substring(7));
                }else{
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            }else{
                finalText.append(texts[i]);
            }
        }
        return finalText.toString();
    }
}
