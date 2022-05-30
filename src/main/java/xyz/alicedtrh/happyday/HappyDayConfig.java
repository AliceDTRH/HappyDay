package xyz.alicedtrh.happyday;

import redempt.redlib.config.annotations.Comment;

public class HappyDayConfig {

    @Comment("(c) AliceDTRH 2022")

    @Comment("This is used to keep track of the configuration file version, please don't touch it.")
    public static String config_version = "2.0.0";

    @Comment("This is used to debug the plugin, I suggest you don't touch this unless I specifically ask you to.")
    public static boolean DEBUG = true;

    @Comment("You can touch things below: ")
    @Comment("When false, disables the entire mod. (default: true)")
    public static boolean ENABLED = true;

    @Comment("The world that HappyDay is active on. (Default: world)")
    public static String activeWorld = "world";

    @Comment("Turn off all log messages except for errors (default: false)")
    public static boolean QUIET = false;

    @Comment("Times used internally by the mod to figure out when night and day are")
    @Comment("Default: DAY_TIME=23460, NIGHT_TIME=12542")
    public static float DAY_TIME = 23460;
    public static float NIGHT_TIME = 12542;

}
