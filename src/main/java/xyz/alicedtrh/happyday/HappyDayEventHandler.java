package xyz.alicedtrh.happyday;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

import static xyz.alicedtrh.happyday.HappyDayConfig.activeWorld;

public final class HappyDayEventHandler implements Listener {
    HappyDayDebounce debouncer = new HappyDayDebounce();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTimeSkipEvent(TimeSkipEvent event) {
        debouncer.debounce(() -> HappyDay.monsterRemover.schedule(Bukkit.getWorld(activeWorld)), 20);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        World world = Objects.requireNonNull(Bukkit.getWorld(activeWorld));
        // The player hasn't quit at this point so the player-count should be 1 if this is the last person
        if(world.getPlayerCount() <= 1) {
            HappyDay.setSuspended(true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!HappyDay.isSuspended()) {
            return;
        }
        HappyDay.setSuspended(false);
    }
}