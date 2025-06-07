package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.listeners.SimpleChallengeRelatedListeners;
import de.lioncraft.lionapi.teams.Team;
import de.lioncraft.lionutils.data.ChallengesData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
        if (!ChallengesData.getInstance().isSharedHearts()) return;
        if (e.getEntity() instanceof Player p) {
            List<Player> list = new ArrayList<>();
            if (ChallengesData.getInstance().isGlobalShared()) {
                list.addAll(Bukkit.getOnlinePlayers());
            } else {
                Team t = Team.getTeam(p);
                if (t != null) t.getPlayers().forEach(player -> {
                    if (player.isOnline()){
                        list.add(player.getPlayer());
                    }
                });
            }
            if (p.getHealth()-e.getFinalDamage()<=0) SimpleChallengeRelatedListeners.PlayerThatDied = p.getName();
            for (Player player : list){
                if (player == p) continue;
                player.setHealth(getValue(p.getHealth()-e.getFinalDamage()));
                player.playSound(Sound.sound(Key.key("entity.player.hurt"), Sound.Source.PLAYER, 0.5f, 1.1f));
            }
        }
    }
    private double getValue(double d){
        if (d < 0) return 0;
        if (d > 20) return 20;
        return d;
    }
    @EventHandler
    public void onRegain(EntityRegainHealthEvent e){
        if (!ChallengesData.getInstance().isSharedHearts()) return;
        if (e.getEntity() instanceof Player p) {
            int lastRegenTick = 0;
            List<Player> list = new ArrayList<>();
            if (ChallengesData.getInstance().isGlobalShared()) {
                lastRegenTick = lastGlobalRegen;
                list.addAll(Bukkit.getOnlinePlayers());
            } else {
                Team t = Team.getTeam(p);
                lastRegenTick = Objects.requireNonNullElse(lastTeamRegen.get(t), 0);
                if (t != null) t.getPlayers().forEach(player -> {
                    if (player.isOnline()){
                        list.add(player.getPlayer());
                    }
                });
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
            }
            if (ChallengesData.getInstance().isGlobalShared()){
                lastGlobalRegen = Bukkit.getServer().getCurrentTick();
            }else{
                lastTeamRegen.put(Team.getTeam(p), Bukkit.getServer().getCurrentTick());
            }
        }
    }

    private static HashMap<Team, Integer> lastTeamRegen = new HashMap<>();
    private static int lastGlobalRegen;
}
