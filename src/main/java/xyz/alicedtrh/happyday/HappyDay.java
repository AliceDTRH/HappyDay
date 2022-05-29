package xyz.alicedtrh.happyday;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.config.ConfigManager;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static xyz.alicedtrh.happyday.HappyDayConfig.*;

public final class HappyDay extends JavaPlugin {
    public static HappyDayMonsterRemover monsterRemover = new HappyDayMonsterRemover();
    private static boolean suspended;

    public static @NotNull Logger log() {
        @NotNull Logger logger = getPlugin(HappyDay.class).getLogger();
        if(QUIET) {
            logger.setLevel(Level.WARNING);
        }
        return logger;
    }

    public static boolean isSuspended() {
        return suspended;
    }

    public static void setSuspended(boolean suspended) {
        HappyDay.suspended = suspended;

        if(suspended) {
            log().info("No players online. Suspending plugin");
            monsterRemover.debouncer.stopAllTasks();
        } else {
            log().info("Players are online. Enabling plugin");
            monsterRemover.schedule(Bukkit.getWorld(activeWorld));
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        ConfigManager config = ConfigManager.create(this);
        config.target(HappyDayConfig.class);
        config.saveDefaults();
        config.load();

        if(!ENABLED) {
            log().info("Plugin disabled by configuration.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        Objects.requireNonNull(Bukkit.getWorld(activeWorld));

        getServer().getPluginManager().registerEvents(new HappyDayEventHandler(), this);

        setSuspended(true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
