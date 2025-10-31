package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.playerSettings.PlayerSettings;
import de.lioncraft.lionutils.inventories.PlayerSettingsGUI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SettingsInvListeners implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent e){
        if(e.getCurrentItem() != null){
            if(e.getClickedInventory() != null){
                if(playerAddToSettingsInv.contains(e.getClickedInventory())){
                    e.setCancelled(true);
                    if(e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)){
                        OfflinePlayer p = ((SkullMeta)e.getCurrentItem().getItemMeta()).getOwningPlayer();
                        if(p != null){
                            PlayerSettings.getSettings(null).cloneTo(p);
                            PlayerSettingsGUI.openSettingsUI(e.getWhoClicked(), p);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        playerAddToSettingsInv.remove(e.getInventory());
    }

    private static final List<Inventory> playerAddToSettingsInv = new ArrayList<>();
    public static void addPlayerAddToSettingsInv(Inventory inventory){
        playerAddToSettingsInv.add(inventory);
    }
}
