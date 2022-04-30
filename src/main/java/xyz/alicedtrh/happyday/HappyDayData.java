package xyz.alicedtrh.happyday;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class HappyDayData {
	private static volatile HappyDayData instance;
	private Integer removedMobs = 0;
	private UpgradeableSpawnersDependency UpgradeableSpawners;
	private World world;
	private String _worldName;
	static final Long DELAY = 1200L; // 20 ticks (20tps) * 60 seconds = 1 minute
	private boolean suspended = false;

	private HappyDayData() {
	}

	public Integer getRemovedMobs() {
		return this.removedMobs;
	}

	public void increaseRemovedMobs() {
		this.removedMobs = this.removedMobs + 1;
	}

	public void resetRemovedMobs() {
		this.removedMobs = 0;
	}

	public UpgradeableSpawnersDependency getUpgradeableSpawners() {
		return UpgradeableSpawners;
	}

	public void setUpgradeableSpawners(UpgradeableSpawnersDependency upgradeableSpawners) {
		UpgradeableSpawners = upgradeableSpawners;
	}

	public World getWorld() {
		if (this.world == null) {
			this.world = Bukkit.getServer().getWorld(getWorldName());
		}
		return world;
	}

	public String getWorldName() {
		if(_worldName == null) {
			_worldName = HappyDay.getPlugin(HappyDay.class).getConfig().getString("world", "world");
		}
		return _worldName;
	}

	public static HappyDayData getInstance() {
		// By using a local variable we can prevent unneeded lookups, improving
		// performance up to 40%.
		// https://youtu.be/tSZn4wkBIu8?t=459 for more information.
		HappyDayData _instance = instance;
		if (_instance == null) {
			synchronized (HappyDayData.class) {
				// _instance is not always true. Do not remove.
				if (_instance == null) {
					instance = _instance = new HappyDayData();
				}
			}
		}
		return _instance;
	}

	/**
	 * @return the suspended state
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * @param suspended suspend the plugin
	 */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
}