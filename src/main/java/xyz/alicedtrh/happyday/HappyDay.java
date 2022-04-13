package xyz.alicedtrh.happyday;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class HappyDay extends JavaPlugin {
	static final Long delay = 1200L; // 20 ticks (20tps) * 60 seconds = 1 minute
	private static Boolean _hasBeenNightTime = false;
	private Integer removedMobs = 0;
	private UpgradeableSpawnersDependency UpgradeableSpawners;

	/**
	 * @return the removedMobs
	 */
	public Integer getRemovedMobs() {
		return removedMobs;
	}

	public void increaseRemovedMobs() {
		this.removedMobs++;
	}

	public void resetRemovedMobs() {
		this.removedMobs = 0;
	}

	/**
	 * @return the hasBeenNightTime
	 */
	static Boolean getHasBeenNightTime() {
		HappyDay.getPlugin(HappyDay.class).getLogger().finer("hasBeenNightTime get: " + _hasBeenNightTime.toString());
		return _hasBeenNightTime;
	}

	/**
	 * @param hasBeenNightTime the hasBeenNightTime to set
	 */
	static void setHasBeenNightTime(Boolean hasBeenNightTime) {
		HappyDay.getPlugin(HappyDay.class).getLogger().finer("hasBeenNightTime set: " + hasBeenNightTime.toString());
		HappyDay._hasBeenNightTime = hasBeenNightTime;
	}

	@Override
	public void onEnable() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskTimer(this, () -> runCheckTask(), delay, delay);
		
		UpgradeableSpawners = new UpgradeableSpawnersDependency();
		
		if(getConfig().getBoolean("debug") == true) {
			getLogger().setLevel(Level.FINEST);
		}

		getServer().getPluginManager().registerEvents(new TimeSkipEventListener(), this);

		saveDefaultConfig();
	}

	/**
	 * @param scheduler
	 * @throws IllegalArgumentException
	 */
	public void runCheckTask() {
		String worldName = getConfig().getString("world", "world");
		World world = Bukkit.getServer().getWorld(worldName); // add try catch
		Integer diff = (int) world.getTime();
		getLogger().info(diff.toString());
		getLogger().finer("CheckTask running");
		if (!world.isDayTime()) {
			HappyDay.setHasBeenNightTime(true);
		}
		if (world.isDayTime() && getHasBeenNightTime()) {
			HappyDay.setHasBeenNightTime(false);
			this.getServer().getScheduler().runTaskLater(this, () -> {
				if (world.isDayTime()) {
					removeMonsters(worldName);
				} else {
					getLogger().info("Skipping mob removal because it's not day anymore.");
				}
			}, getDelayUntilDay(worldName));
		}
	}

	private long getDelayUntilDay(String worldName) {
		World world = Bukkit.getServer().getWorld(worldName);
		Integer diff = (int) world.getTime();
		getLogger().info(diff.toString());
		return 200;
		
	}

	private void removeMonsters(String worldName) {
		getLogger().info("Removing monsters.");
		World world = this.getServer().getWorld(worldName); // add try catch
		world.getEntitiesByClasses(Stray.class, Zombie.class, Spider.class, Skeleton.class, Creeper.class,
				Drowned.class, CaveSpider.class, Husk.class, Phantom.class, Enderman.class, Witch.class, Slime.class)
				.forEach(entity -> {
					if (shouldRemoveMonster((LivingEntity) entity)) {
						if (entity.isValid()) {
							this.increaseRemovedMobs();
							entity.remove();
						}
					}
				});
		getLogger().info("Removed "+this.getRemovedMobs()+" monsters.");
		this.resetRemovedMobs();
	}

	/**
	 * @param monster
	 * @return boolean Whether the monster should be removed.
	 */
	private boolean shouldRemoveMonster(Entity monster) {
		// UpgradeAbleSpawners compatibility
		if(UpgradeableSpawners.isSpawnedBySpawner(monster)) {
			return false;
		}
		
		// Is the monster spawned in an unnatural way? (Mob spawner, etc.)
		if (monster.fromMobSpawner() || (monster.getEntitySpawnReason() != SpawnReason.NATURAL
				&& monster.getEntitySpawnReason() != SpawnReason.DEFAULT)) {
			return false;
		}

		// Does it have a custom name?
		if (monster.customName() != null) {
			return false;
		}

		// Is it currently inside a cave?
		if (monster.getLocation().getBlock().getType() == Material.CAVE_AIR) {
			return false;
		}

		return true;
	}
}
