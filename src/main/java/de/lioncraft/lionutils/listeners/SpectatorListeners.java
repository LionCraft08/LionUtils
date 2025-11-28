package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionutils.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorListeners implements Listener {
    public static final NamespacedKey spectatorItemKey = NamespacedKey.fromString("lionutils_spectator_interactions", Main.getPlugin());
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if (e.getItem() != null){
            if (e.getItem().getPersistentDataContainer().has(spectatorItemKey)){

            }
        }
    }
}
