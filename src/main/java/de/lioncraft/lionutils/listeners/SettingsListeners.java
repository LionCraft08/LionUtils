package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.events.saveDataEvent;
import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.defaultMessages;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.data.ChallengesData;
import de.lioncraft.lionutils.inventories.PlayerSettingsGUI;
import de.lioncraft.lionutils.utils.Settings;
import de.lioncraft.lionutils.utils.status.StatusSettings;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsListeners implements Listener {
    @EventHandler
    public void onChat(AsyncChatEvent e){
        if (!Settings.getSettings(e.getPlayer()).canChat()) {
            e.setCancelled(true);
            LionChat.sendError("You are not allowed to chat!", e.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if (!Settings.getSettings(e.getPlayer()).canMove()) {
            if (e.hasChangedPosition()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(BlockBreakEvent e){
        if (!Settings.getSettings(e.getPlayer()).canMineBlocks()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onHit(PrePlayerAttackEntityEvent e){
        if (!Settings.getSettings(e.getPlayer()).canHitEntities()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInvClick(InventoryClickEvent e){
        if(e.getCurrentItem() != null){
            if(e.getClickedInventory() != null){
                if(playerAddToSettingsInv.contains(e.getClickedInventory())){
                    e.setCancelled(true);
                    if(e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)){
                        OfflinePlayer p = ((SkullMeta)e.getCurrentItem().getItemMeta()).getOwningPlayer();
                        if(p != null){
                            Settings.getSettings(null).cloneTo(p);
                            PlayerSettingsGUI.openSettingsUI(e.getWhoClicked(), p);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Settings.getSettings(e.getPlayer()).update();
    }
    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        playerAddToSettingsInv.remove(e.getInventory());
    }
    @EventHandler
    public void onGameMode(PlayerGameModeChangeEvent e){
        if (e.getNewGameMode().equals(GameMode.ADVENTURE)||e.getNewGameMode().equals(GameMode.SURVIVAL)) {
            Settings.getSettings(e.getPlayer()).update();
        }
    }
    @EventHandler
    public void onSave(saveDataEvent e){
        try {
            Settings.serializeAll();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }finally {
            try {
                StatusSettings.serializeAll();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }finally {
                ChallengesData.save();
            }
        }

    }
    private static final List<Inventory> playerAddToSettingsInv = new ArrayList<>();
    public static void addPlayerAddToSettingsInv(Inventory inventory){
        playerAddToSettingsInv.add(inventory);
    }

}
