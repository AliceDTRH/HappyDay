package xyz.alicedtrh.happyday;

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
		HappyDay.getPlugin(HappyDay.class).getLogger().warning("hasBeenNightTime get: " + _hasBeenNightTime.toString());
		return _hasBeenNightTime;
	}

	/**
	 * @param hasBeenNightTime the hasBeenNightTime to set
	 */
	static void setHasBeenNightTime(Boolean hasBeenNightTime) {
		HappyDay.getPlugin(HappyDay.class).getLogger().warning("hasBeenNightTime set: " + hasBeenNightTime.toString());
		HappyDay._hasBeenNightTime = hasBeenNightTime;
	}

	@Override
	public void onEnable() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskTimer(this, () -> runCheckTask(), delay, delay);
		
		if(Bukkit.getServer().getPluginManager().getPlugin("UpgradeableSpawners") == null) {
			getLogger().warning("UpgradeableSpawners not found. Support for UpgradeableSpawners is not active.");
		}

		getServer().getPluginManager().registerEvents(new TimeSkipEventListener(), this);

		saveDefaultConfig();
	}

	/**
	 * @param scheduler
	 * @throws IllegalArgumentException
	 */
	public void runCheckTask() throws IllegalArgumentException {
		String worldName = getConfig().getString("world", "world");
		World world = Bukkit.getServer().getWorld(worldName); // add try catch
		getLogger().warning("CheckTask running");
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
			}, delay / 4);
		}
	}

	private void removeMonsters(String worldName) {
		// TODO Auto-generated method stub
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
		if (Bukkit.getServer().getPluginManager().getPlugin("UpgradeableSpawners") != null) {
			if(me.angeschossen.upgradeablespawners.api.UpgradeableSpawnersAPI.isSpawnedBySpawner(monster)) {
				return false;
			}
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
