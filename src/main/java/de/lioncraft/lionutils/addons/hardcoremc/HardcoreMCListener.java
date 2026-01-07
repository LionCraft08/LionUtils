package de.lioncraft.lionutils.addons.hardcoremc;

import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import de.lioncraft.lionutils.addons.sharedhearts.SharedHeartsAddon;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class HardcoreMCListener implements Listener {
    @EventHandler
    public void onJoin(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p){
            switch (HardcoreMCAddon.getInstance().getSettings().getStringValue("regeneration_mode")){
                case "NONE" -> {
                    setMaxHealth(p, p.getHealth()-event.getFinalDamage());
                    for (Player player : SharedHeartsAddon.getInstance().getAffectedPlayers(p)){
                        if (player != p){
                            setMaxHealth(player, p.getHealth()-event.getFinalDamage());
                        }
                    }
//                    if (p.getAttribute(Attribute.MAX_HEALTH) == null)
//                        p.registerAttribute(Attribute.MAX_HEALTH);
//                    p.getAttribute(Attribute.MAX_HEALTH).setBaseValue();
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (!HardcoreMCAddon.getInstance().getSettings().getBoolValue("half_heart")){
            setMaxHealth(event.getPlayer(), 20);
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event){
        if (event.getEntity() instanceof Player p){
            switch (HardcoreMCAddon.getInstance().getSettings().getStringValue("regeneration_mode")){
                case "NONE" -> {
                    setMaxHealth(p, p.getHealth());
                    for (Player player : SharedHeartsAddon.getInstance().getAffectedPlayers(p)){
                        if (player != p){
                            setMaxHealth(player, p.getHealth());
                        }
                    }
                }
                case "EFFECTS" -> {
                    if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSaturation(EntityExhaustionEvent event){
        if (Objects.equals(HardcoreMCAddon.getInstance().getSettings().getStringValue("regeneration_mode"), "NORMAL")) {return;}

        if (event.getExhaustionReason() == EntityExhaustionEvent.ExhaustionReason.REGEN) {

            // Cancel the event so saturation/hunger doesn't drop
            event.setCancelled(true);
        }
    }

    private void setMaxHealth(Player p, double value){
        if (value < 1){ value = 1; }
        if (p.getAttribute(Attribute.MAX_HEALTH) == null)
            p.registerAttribute(Attribute.MAX_HEALTH);
        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(value);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();
        if (HardcoreMCAddon.getInstance().getSettings().getBoolValue("half_heart")){
            setMaxHealth(p, 1);
        }
    }
}
