package de.lioncraft.lionutils.addons.sharedhearts;

import de.lioncraft.lionapi.listeners.SimpleChallengeRelatedListeners;
import de.lioncraft.lionapi.teams.Team;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SharedHeartsListeners implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player p) {
            List<Player> list = new ArrayList<>(SharedHeartsAddon.getInstance().getAffectedPlayers(p));

            //Set the player for the Simple Challenge (LionAPI) to the one that actually got damaged here
            //as the following Method would kill the other Players before this one.
            //this is important, as otherwise the Text Message 'which player died' would display the wrong Player.
            if (p.getHealth()-e.getFinalDamage()<=0) SimpleChallengeRelatedListeners.PlayerThatDied = p.getName();

            for (Player player : list){
                if (player == p) continue;
                if (SharedHeartsAddon.getInstance().getSettings().getBoolValue("sync-damage-only"))
                    player.setHealth(getValue(player.getHealth()-e.getFinalDamage()));
                else
                    player.setHealth(getValue(p.getHealth()-e.getFinalDamage()));
                if(DamageDisplay.getDamageDisplay().isTabListActive()){
                    DamageDisplay.getDamageDisplay().updateTabListDelayed(player);
                }
                player.playSound(Sound.sound(Key.key("entity.player.hurt"), Sound.Source.PLAYER, 0.5f, 1.1f));
            }
        }
    }
    private double getValue(double d){
        if (d < 0) return 0;
        if (d > 20) return 20;
        return d;
    }

    private boolean isGlobal(Player p){
        return SharedHeartsAddon.getInstance().getSettings().getBoolValue("force-global");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRegain(EntityRegainHealthEvent e){
        if (SharedHeartsAddon.getInstance().getSettings().getBoolValue("sync-damage-only")) return;
        if (e.isCancelled()) return;
        if (e.getEntity() instanceof Player p) {
            int lastRegenTick = 0;
            List<Player> list = new ArrayList<>(SharedHeartsAddon.getInstance().getAffectedPlayers(p));
            if (isGlobal(p)) {
                lastRegenTick = lastGlobalRegen;
            } else {
                Team t = Team.getTeam(p);
                lastRegenTick = Objects.requireNonNullElse(lastTeamRegen.get(t), 0);
            }
            if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)){
                int sinceLastRegen = Bukkit.getServer().getCurrentTick()-lastRegenTick;
                if (e.isFastRegen()){
                    if (sinceLastRegen < 10){
                        e.setCancelled(true);
                        return;
                    }
                }else{
                    if (sinceLastRegen < 80){
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            for (Player player : list){
                if (player == p) continue;
                player.setHealth(getValue(p.getHealth()+e.getAmount()));
                if(DamageDisplay.getDamageDisplay().isTabListActive()){
                    DamageDisplay.getDamageDisplay().updateTabListDelayed(player);
                }
            }
            if (isGlobal(p)){
                lastGlobalRegen = Bukkit.getServer().getCurrentTick();
            }else{
                lastTeamRegen.put(Team.getTeam(p), Bukkit.getServer().getCurrentTick());
            }
        }
    }

    private static HashMap<Team, Integer> lastTeamRegen = new HashMap<>();
    private static int lastGlobalRegen;
}
