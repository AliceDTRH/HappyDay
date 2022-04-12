package xyz.alicedtrh.happyday;

import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class TimeSkipEventListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTimeSkipEvent(TimeSkipEvent event) {
		HappyDay happyday = HappyDay.getPlugin(HappyDay.class);
		happyday.getLogger().warning("Running checktask");
		happyday.runCheckTask();
		happyday.getLogger().warning("Running checktask*");
	} 
}
