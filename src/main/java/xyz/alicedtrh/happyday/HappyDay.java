package xyz.alicedtrh.happyday;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.config.ConfigManager;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static xyz.alicedtrh.happyday.HappyDayConfig.*;

public final class HappyDay extends JavaPlugin {
    final static HappyDayMonsterRemover monsterRemover = new HappyDayMonsterRemover();
    private static boolean suspended = false;

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
    static @NotNull Logger log() {
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
        if(!EMPTY_SUSPEND) {
            suspended = false;
        }

        HappyDay.suspended = suspended;

        if(suspended) {
            log().info("No players online. Suspending plugin");
            monsterRemover.debouncer.stopAllTasks();
        } else {
            log().info("Players are online. Enabling plugin");
            monsterRemover.schedule(WORLD);
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupConfiguration();

        if(!ENABLED) {
            log().info("Plugin disabled by configuration.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        //Fail early if there's no valid world setup.
        try {
            Objects.requireNonNull(WORLD);
        } catch (NullPointerException e) {
            log().severe("Couldn't find a valid world! Make sure your configuration is correct.");
            throw new InvalidWorldException("Couldn't find a valid world! Make sure your configuration is correct.", e);
        }


        getServer().getPluginManager().registerEvents(new HappyDayEventHandler(), this);

        setSuspended(true);
    }

    private void setupConfiguration() {
        ConfigManager config = ConfigManager.create(this);
        config.addConverter(World.class, Bukkit::getWorld, World::getName);
        config.target(HappyDayConfig.class);
        config.saveDefaults();
        config.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
