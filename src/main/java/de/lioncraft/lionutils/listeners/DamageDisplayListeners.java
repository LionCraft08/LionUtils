package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DamageDisplayListeners implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player p){
            if(DamageDisplay.getDamageDisplay().isChatActive()){
                if (e.getFinalDamage() <= 0) return;

                Component health = Component.text("");
                Component cause = Component.text("");
                Component heart = Component.text("\uD83D\uDC94", TextColor.color(255, 107, 111));
                TextColor white = TextColor.color(255, 255, 255);
                if(DamageDisplay.getDamageDisplay().isNumberOfHearts()){
                    heart = Component.text("❤", TextColor.color(255, 0, 0));
                }
                if(DamageDisplay.getDamageDisplay().isChatShowPlayerHealth()){
                    health = Component.text("(", white).append(heart.append(Component.text(getRoundedValue(p.getHealth()-e.getFinalDamage())))).append(Component.text(")", white));
                }
                if(DamageDisplay.getDamageDisplay().isChatShowCause()){
                    cause = Component.text("durch ");
                    if(e.getDamageSource().getCausingEntity() != null){
                        if(e.getDamageSource().getCausingEntity().getType() == EntityType.PLAYER){
                            cause = cause.append(Component.text(e.getDamageSource().getCausingEntity().getName()));
                        }else cause = cause.append(Component.translatable(e.getDamageSource().getCausingEntity().getType().translationKey()));
                        cause = cause.append(Component.text(" (")).append(Component.translatable(e.getDamageSource().getDamageType().getTranslationKey())).append(Component.text(")"));
                    }else cause = cause.append(Component.translatable(e.getDamageSource().getDamageType().getTranslationKey()));
                }
                Component prefix = Component.text("[", white).append(Component.text("DMG", TextColor.color(255, 128, 0))).append(Component.text("] ", white));
                Component c = prefix.append(p.displayName().append(health).append(Component.text(": ", white).append(heart).append(Component.text(getRoundedValue(e.getFinalDamage())).append(Component.text(" -> ", white).append(cause)))));
                Bukkit.broadcast(c);
            }
        }
    }
    private double getRoundedValue(double d){
        if(DamageDisplay.getDamageDisplay().isNumberOfHearts()) d /= 2;
        return ((double) Math.round(d * 10))/10;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(DamageDisplay.getDamageDisplay().isTabListActive()){
            DamageDisplay.getDamageDisplay().updateTabListDelayed(e.getPlayer());
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onJoin(EntityRegainHealthEvent e){
        if (e.getEntity() instanceof Player p)
            if(DamageDisplay.getDamageDisplay().isTabListActive()){
                DamageDisplay.getDamageDisplay().updateTabListDelayed(p);
            }
    }
    @EventHandler(ignoreCancelled = true)
    public void onJoin(EntityDamageEvent e){
        if (e.getEntity() instanceof Player p)
            if(DamageDisplay.getDamageDisplay().isTabListActive()){
                DamageDisplay.getDamageDisplay().updateTabListDelayed(p);
            }
    }
}
