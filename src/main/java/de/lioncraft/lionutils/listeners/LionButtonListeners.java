package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.events.invs.LionButtonClickEvent;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.inventories.ChallengesGUI;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.inventories.PlayerSettingsGUI;
import de.lioncraft.lionutils.inventories.opUtils;
import de.lioncraft.lionutils.utils.Settings;
import de.lioncraft.lionutils.utils.status.Inventories;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LionButtonListeners implements Listener {
    @EventHandler
    public void onClick(LionButtonClickEvent e){
        switch (e.getID()){
            case "lionutils_open_status_menu"-> Inventories.openMainMenu(e.getPlayer());
            case "lionutils_open_op_utils" -> opUtils.openUI(e.getPlayer());
            case "lionutils_open_health_display_settings" -> DamageDisplay.open(e.getPlayer());
            case "lionutils_heal" -> {
                int i = 0;
                for(Player p : Bukkit.getOnlinePlayers()){
                    heal(p);
                    i++;
                    DamageDisplay.getDamageDisplay().updateTabListDelayed(p);
                }
                LionChat.sendSystemMessage("You have healed " + i + " Players!", e.getPlayer());
                e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 1.5f);

            }
            case "lionutils_challenges"-> ChallengesGUI.open(e.getPlayer());
            case "lionutils_tp" -> {
                Player clicker = e.getPlayer();
                int i = 0;
                for (Player p : Bukkit.getOnlinePlayers()){
                    if (p != clicker){
                        i++;
                        p.teleport(clicker.getLocation());
                    }
                }
                if (i>0){
                    LionChat.sendSystemMessage("Teleported "+i+" Players to you", clicker);
                    clicker.playSound(clicker, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 1.0f);
                }else{
                    LionChat.sendSystemMessage("Could not find any Player to teleport", clicker);
                    clicker.playSound(clicker, Sound.ENTITY_SILVERFISH_HURT, 1.0f, 1.0f);
                }
            }
            case "lionutils_player_management" -> PlayerSettingsGUI.openSelectUI(e.getPlayer());
            case "lionutils_update_tablist"->{
                Component c = DamageDisplay.getDamageDisplay().updateTabList();
                e.getPlayer().sendMessage(c);
                e.getPlayer().playSound(e.getPlayer(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
            case "lionutils_open_settings_ui"->{
                if (e.getData().isBlank()){
                    PlayerSettingsGUI.openSettingsUI(e.getPlayer(), null);
                }else{
                    OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(e.getData()));
                    if(e.e().getClick().isLeftClick()){
                        PlayerSettingsGUI.openSettingsUI(e.getPlayer(), p);
                    }else if (e.e().isRightClick()){
                        Settings.removeSetting(p);
                        PlayerSettingsGUI.openSelectUI(e.getPlayer());
                    }
                }
            }
            case "lionutils_open_select_player_ui" ->{
                e.getPlayer().openInventory(PlayerSettingsGUI.addPlayerGUI());
            }
        }
    }
    public static void heal(Player p){
        p.setHealth(20);
        p.setSaturation(5);
        p.setFoodLevel(20);
    }
}
