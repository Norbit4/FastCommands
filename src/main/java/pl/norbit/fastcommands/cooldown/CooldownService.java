package pl.norbit.fastcommands.cooldown;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownService {
    private final Map<String, Long> cooldowns = new HashMap<>();

    public boolean isOnCooldown(CommandSender sender, String commandName) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        String key = player.getUniqueId() + ":" + commandName;

        Long expireAt = cooldowns.get(key);

        return expireAt != null && expireAt > System.currentTimeMillis();
    }

    public long getRemainingSeconds(CommandSender sender, String commandName) {
        if (!(sender instanceof Player player)) {
            return 0;
        }

        String key = player.getUniqueId() + ":" + commandName;

        Long expireAt = cooldowns.get(key);

        if (expireAt == null) {
            return 0;
        }

        return Math.max(0, (expireAt - System.currentTimeMillis()) / 1000);
    }

    public void setCooldown(CommandSender sender, String commandName, int seconds) {
        if (!(sender instanceof Player player)) {
            return;
        }

        String key = player.getUniqueId() + ":" + commandName;

        cooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
    }
}
