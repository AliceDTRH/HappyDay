package xyz.alicedtrh.happyday;

import org.bukkit.World;

import static xyz.alicedtrh.happyday.HappyDayConfig.NIGHT_TIME;
import static xyz.alicedtrh.happyday.HappyDayConfig.DAY_TIME;

public class HappyDayTimeUtils {
    World world;

    public HappyDayTimeUtils(World world) {
        this.world = world;
    }

    public boolean isDayTime () {
        return !isNightTime();
    }

    public boolean isNightTime () {
        return world.getTime() > NIGHT_TIME && world.getTime() < DAY_TIME ;
    }


    /**
     * @return Time until day in ticks
     */
    public long getTimeUntilDay() {
        float result = (DAY_TIME - world.getTime()) % 24000;
        while (result < 0) {result = result + 24000;}
        return (long) result;
    }

}
