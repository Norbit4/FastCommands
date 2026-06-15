package pl.norbit.fastcommands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.fastcommands.commands.ReloadCommand;
import pl.norbit.fastcommands.settings.Config;

import java.util.logging.Logger;

public final class FastCommands extends JavaPlugin {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static FastCommands instance;

    @Override
    public void onEnable() {
        setInstance(this);
        Config.load(false, null);

        checkPapi();
        infoMessage();

        getCommand("fastcommands").setExecutor(new ReloadCommand());
    }

    private void infoMessage(){
        Logger log = getServer().getLogger();
        log.info("");
        log.info("FastCommands by Norbit4!");
        log.info("Website: https://github.com/Norbit4");
        log.info("");
    }

    private void checkPapi() {
        PluginManager pm = getServer().getPluginManager();
        Plugin papiPlugin = pm.getPlugin("PlaceholderAPI");

        if(papiPlugin != null){
            Config.setPapiEnable(papiPlugin.isEnabled());
        }
    }
}
