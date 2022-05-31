package xyz.alicedtrh.happyday;

import org.bukkit.event.entity.CreatureSpawnEvent;
import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigMappable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT;
import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.NATURAL;

@ConfigMappable
public class HappyDayConfig {

    @Comment("(c) AliceDTRH 2022")

    @Comment("This is used to keep track of the configuration file version, please don't touch it.")
    public static String config_version = "2.0.1";

    @Comment("This is used to debug the plugin, I suggest you don't touch this unless I specifically ask you to.")
    public static boolean DEBUG = false;

    @Comment("Turn off all log messages except for errors (default: false)")
    public static boolean QUIET = false;

    @Comment("You can touch things below: ")
    @Comment("When false, disables the entire mod. (default: true)")
    public static boolean ENABLED = true;

    @Comment("The world that HappyDay is active on. (Default: world)")
    public static String WORLD = "world";

    @Comment("Times used internally by the mod to figure out when night and day are")
    @Comment("This is an advanced option, don't touch this unless you know what you're doing.")
    @Comment("Default: DAY_TIME=23460, NIGHT_TIME=12542")
    public static float DAY_TIME = 23460;
    public static float NIGHT_TIME = 12542;

    @Comment("Suspend plugin when there's no players online (default: true)")
    public static boolean EMPTY_SUSPEND = true;

    @Comment("Spawnreasons for which the mob shouldn't be deleted.")
    @Comment("https://jd.bukkit.org/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html for more info")
    @Comment("This is an advanced option, don't touch this unless you know what you're doing.")
    public static List<CreatureSpawnEvent.SpawnReason> SPAWNREASONS = Arrays.asList(NATURAL, DEFAULT);
}
