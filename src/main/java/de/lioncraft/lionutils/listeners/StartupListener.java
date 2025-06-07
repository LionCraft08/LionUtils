package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.inventories.MainGUI;
import de.lioncraft.lionutils.inventories.PlayerSettingsGUI;
import de.lioncraft.lionutils.utils.InvWatcher;
import de.lioncraft.lionutils.utils.Settings;
import de.lioncraft.lionutils.utils.status.StatusChecker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerLoadEvent;

public class StartupListener implements Listener {
    @EventHandler
    public void onStart(ServerLoadEvent e){
        InvWatcher.Initialize();
        PlayerSettingsGUI.Initialize();
        MainGUI.Initialize();
        StatusChecker.thisTask = new StatusChecker().runTaskTimer(Main.getPlugin(), StatusChecker.afkTime, 200);

        Settings.deserializeAll();
        de.lioncraft.lionutils.utils.status.StatusSettings.deserializeAll();
        DamageDisplay.deserialize();
        DamageDisplay.Init();

        for (World w : Bukkit.getWorlds()){
            w.setViewDistance(32);
        }
    }
    @EventHandler
    public void onAPIEnable(PluginEnableEvent e){
        if(e.getPlugin().getName().equals("lionAPI")){

        }
    }
}
