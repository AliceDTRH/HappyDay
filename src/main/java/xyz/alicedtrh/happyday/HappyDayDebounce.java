package xyz.alicedtrh.happyday;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;

import static xyz.alicedtrh.happyday.HappyDayConfig.*;

public class HappyDayDebounce {
    HashSet<BukkitTask> bukkitTasks = new HashSet<>();

    public void debounce (Runnable runnable, long delay) {
        if(bukkitTasks.isEmpty()) {
            bukkitTasks.add(Bukkit.getScheduler().runTaskLater(HappyDay.getPlugin(HappyDay.class), runnable, delay));
        } else {
            stopAllTasks();
            bukkitTasks.add(Bukkit.getScheduler().runTaskLater(HappyDay.getPlugin(HappyDay.class), runnable, delay));
        }
    }

    public void stopAllTasks() {
        bukkitTasks.forEach((bukkitTask) -> {
            if(Bukkit.getScheduler().isQueued(bukkitTask.getTaskId())) {
                if(DEBUG) HappyDay.log().info("Cancelled "+bukkitTask.getTaskId());
                bukkitTask.cancel();
            }
        });
        // If any tasks are still in the HashSet, they already finished.
        bukkitTasks.clear();
    }


}