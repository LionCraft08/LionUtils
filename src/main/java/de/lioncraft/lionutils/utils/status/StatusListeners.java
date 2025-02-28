package de.lioncraft.lionutils.utils.status;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.lioncraft.lionutils.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;
import java.util.Map;

public class StatusListeners implements Listener {
    public static NamespacedKey disabledClickItems = new NamespacedKey(Main.getPlugin(), "disabled-click");
    @EventHandler
    public void BookEvent(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }
        if(e.getCurrentItem() != null){
            if(e.getCurrentItem().getPersistentDataContainer().has(disabledClickItems)){
                e.setCancelled(true);
            }
            if(e.getInventory() instanceof AnvilInventory i){
                if (anvilPartMap.containsKey(i)){
                    e.setCancelled(true);
                    if(e.getView() instanceof AnvilView view){
                        if(e.getCurrentItem().equals(StatusSettings.getAllow())){
                            Status s = anvilStatusMap.get(i);
                            int num = anvilPartMap.get(i);
                            s.setText(num, view.getRenameText());
                            anvilPartMap.remove(i);
                            anvilStatusMap.remove(i);
                            i.setFirstItem(null);
                            i.setSecondItem(null);
                            i.setResult(null);
                            Inventories.openConfigurePartMenu(e.getWhoClicked(), num, s);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getInventory() instanceof AnvilInventory i){
            if (anvilPartMap.containsKey(i)){
                Status s = anvilStatusMap.get(i);
                int part = anvilPartMap.get(i);
                i.setFirstItem(null);
                i.setSecondItem(null);
                i.setResult(null);
                new openStatusConfigureGUILater((Player) e.getPlayer(), part, s).runTaskLater(Main.getPlugin(), 1);
                anvilPartMap.remove(i);
                anvilStatusMap.remove(i);
            }
        }
    }

    private static final HashMap<AnvilInventory, Integer> anvilPartMap = new HashMap<>();
    private static final HashMap<AnvilInventory, Status> anvilStatusMap = new HashMap<>();
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e){
        if(anvilPartMap.containsKey(e.getInventory())){
            e.getView().setRepairCost(0);
            if(e.getView().getRenameText().isBlank()){
                e.setResult(StatusSettings.getDeny());
            }else e.setResult(StatusSettings.getAllow());

        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        StatusSettings.getSettings(e.getPlayer()).resetAFKTimer();
        StatusSettings.getSettings(e.getPlayer()).update();
    }
    @EventHandler
    public void onJoin(PlayerPostRespawnEvent e){
        StatusSettings.getSettings(e.getPlayer()).update();
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        StatusSettings.getSettings(e.getPlayer()).resetAFKTimer();
    }
    public static void registerNewInventory(AnvilInventory i, int part, Status status){
        anvilStatusMap.put(i, status);
        anvilPartMap.put(i, part);
    }
}
