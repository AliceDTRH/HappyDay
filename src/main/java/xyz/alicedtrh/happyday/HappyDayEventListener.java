package xyz.alicedtrh.happyday;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class HappyDayEventListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTimeSkipEvent(TimeSkipEvent event) {
		HappyDay happyday = HappyDay.getPlugin(HappyDay.class);
		happyday.getLogger().finer("Running checktask");
		happyday.runCheckTask();
		happyday.getLogger().finer("Running checktask*");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		if (Bukkit.getOnlinePlayers().size() < 1) {
			HappyDayData.getInstance().setSuspended(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (HappyDayData.getInstance().isSuspended()) {
			return;
		}
		HappyDayData.getInstance().setSuspended(false);
	}
}
