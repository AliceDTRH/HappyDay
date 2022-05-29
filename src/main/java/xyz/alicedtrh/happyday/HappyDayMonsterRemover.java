package xyz.alicedtrh.happyday;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class HappyDayMonsterRemover {
    private static final Set<SpawnReason> VALID_SPAWN_REASONS = Collections.unmodifiableSet(EnumSet.of(SpawnReason.NATURAL, SpawnReason.DEFAULT));
    HappyDayDebounce debouncer = new HappyDayDebounce();

    public void schedule(World world) {
        long scheduledTime = new HappyDayTimeUtils(world).getTimeUntilDay();
        if(new HappyDayTimeUtils(world).isDayTime()) {
            removeMonsters(world); //Always remove monsters after changing time to daytime.
        }
        debouncer.debounce(() -> removeMonsters(world), scheduledTime);
    }

    public void removeMonsters(World world) {
        if(HappyDay.isSuspended()) { return; }
        int removedMobs = 0;
        for (Entity entity : world.getEntitiesByClasses(Stray.class, Zombie.class, Spider.class, Skeleton.class, Creeper.class,
                Drowned.class, CaveSpider.class, Husk.class, Phantom.class, Enderman.class, Witch.class, Slime.class)) {
            if (shouldRemoveMonster(entity)) {
                if (entity.isValid()) {
                    removedMobs++;
                    entity.remove();
                }
            }
        }
        HappyDay.log().info(String.format("Removed %d monsters", removedMobs));
    }

    /**
     * @param monster The monster to check
     * @return boolean Whether the monster should be removed.
     */
    private boolean shouldRemoveMonster(Entity monster) {
        // Was the monster spawned from a UpgradeAbleSpawners spawner? (Requires UpgradeAbleSpawners)
        // TODO: Implement Support for UpgradeableSpawners

        // Was the monster spawned from a mob spawner?
        if (monster.fromMobSpawner()) return false;

        // Is the monster spawned in an unnatural way?
        if (!VALID_SPAWN_REASONS.contains(monster.getEntitySpawnReason())) return false;

        // Does it have a custom name?
        if (monster.customName() != null) return false;

        // Is it currently inside a cave?
        return monster.getLocation().getBlock().getType() != Material.CAVE_AIR;
    }

}
