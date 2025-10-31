package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.events.saveDataEvent;
import de.lioncraft.lionutils.data.ChallengesData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class DataListeners implements Listener {
    @EventHandler
    public void onSave(saveDataEvent e){
        ChallengesData.save();
        try {
            de.lioncraft.lionutils.utils.status.StatusSettings.serializeAll();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
