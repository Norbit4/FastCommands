package pl.norbit.fastcommands.placeholders;


import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderUtil {
    private PlaceholderUtil() {}

    public static String format(String message, Player p){
        return PlaceholderAPI.setPlaceholders(p, message);
    }
}
