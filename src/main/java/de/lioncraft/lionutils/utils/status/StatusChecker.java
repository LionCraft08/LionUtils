package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionutils.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class StatusChecker extends BukkitRunnable {
    public static final int afkTime = Main.getPlugin().getConfig().getInt("settings.status.afktimer");
    public static BukkitTask thisTask;
    @Override
    public void run() {
        for(Map.Entry<UUID, StatusSettings> entry : StatusSettings.getSettings().entrySet()){
            if(entry.getValue().getPlayer().isOnline()){
                if((System.currentTimeMillis()-entry.getValue().getAfktimer())/1000>=afkTime){
                    if(!entry.getValue().isAFK()){
                        if(entry.getValue().isAutoStatus() && entry.getValue().isEnabled())
                            entry.getValue().setAFK(true);
                    }
                }
            }
        }
    }
}
