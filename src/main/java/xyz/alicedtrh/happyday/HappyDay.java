package xyz.alicedtrh.happyday;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class HappyDay extends JavaPlugin {
	private static final Set<SpawnReason> VALID_SPAWN_REASONS = Collections.unmodifiableSet(EnumSet.of(SpawnReason.NATURAL, SpawnReason.DEFAULT));
	private HappyDayData data = HappyDayData.getInstance();

	@Override
	public void onEnable() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskTimer(this, this::runCheckTask, HappyDayData.DELAY, HappyDayData.DELAY);
		/* TODO: Improve task timers.
		    We don't want to run more than one CheckTask at once and we don't need to run it this often. */

		data.setUpgradeableSpawners(new UpgradeableSpawnersDependency());

		if (getConfig().getBoolean("debug")) {
			xyz.alicedtrh.happyday.DebugLogHandler.attachDebugLogger(this);
			getLogger().setLevel(Level.FINEST);
		}

		if (data.getWorld() == null) {
			getLogger().severe(
					"Could not find world \"" + data.getWorldName() + "\", cannot continue without valid world.");
			Bukkit.getPluginManager().disablePlugin(this);
		}

		getServer().getPluginManager().registerEvents(new HappyDayEventListener(), this);

		saveDefaultConfig();
	}

	/**
	 */
	public void runCheckTask() {
		if (data.isSuspended()) {
			// TODO: Disable the timer entirely instead of just skipping over checks.
			getLogger().finer("Plugin is suspended.");
			return;
		}

		getLogger().finer("CheckTask running");
		if (data.getWorld().isDayTime()) {
			this.getServer().getScheduler().runTaskLater(this, () -> {
				if (data.getWorld().isDayTime()) {
					removeMonsters(data.getWorld());
				} else {
					getLogger().finer("Skipping mob removal because it's not day anymore.");
				}
			}, HappyDayData.DELAY / 4);
		}
	}

	private void removeMonsters(World world) {
		getLogger().finer("Removing monsters.");
		world.getEntitiesByClasses(Stray.class, Zombie.class, Spider.class, Skeleton.class, Creeper.class,
				Drowned.class, CaveSpider.class, Husk.class, Phantom.class, Enderman.class, Witch.class, Slime.class)
				.forEach(entity -> {
					if (shouldRemoveMonster((LivingEntity) entity)) {
						if (entity.isValid()) {
							data.increaseRemovedMobs();
							entity.remove();
						}
					}
				});
		getLogger().finer("Removed " + data.getRemovedMobs() + " monsters.");
		data.resetRemovedMobs();
	}

	/**
	 * @param monster The monster to check
	 * @return boolean Whether the monster should be removed.
	 */
	private boolean shouldRemoveMonster(Entity monster) {
		// Was the monster spawned from a UpgradeAbleSpawners spawner? (Requires UpgradeAbleSpawners)
		if (data.getUpgradeableSpawners().isSpawnedBySpawner(monster)) return false;

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
