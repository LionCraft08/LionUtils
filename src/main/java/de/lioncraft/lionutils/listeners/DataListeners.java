package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.events.saveDataEvent;
import de.lioncraft.lionutils.addons.CommandUtilsAddon;
import de.lioncraft.lionutils.addons.hardcoremc.HardcoreMCAddon;
import de.lioncraft.lionutils.addons.sharedhearts.SharedHeartsAddon;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.utils.StructureUtils;
import de.lioncraft.lionutils.utils.spectator.SpectatorManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class DataListeners implements Listener {
    @EventHandler
    public void onSave(saveDataEvent e){
        SpectatorManager.save();
        StructureUtils.save();
        CommandUtilsAddon.save();
        HardcoreMCAddon.save();
        DamageDisplay.save();
        SharedHeartsAddon.save();

        try {
            de.lioncraft.lionutils.utils.status.StatusSettings.serializeAll();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
