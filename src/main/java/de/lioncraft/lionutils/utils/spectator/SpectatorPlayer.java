package de.lioncraft.lionutils.utils.spectator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SpectatorPlayer {
    private Player p;
    private List<ItemStack> hotbar = new ArrayList<>();
    private boolean invisible;

    public SpectatorPlayer(Player p) {
        this.p = p;
    }
    public void enable(){
        p.setAllowFlight(true);
        for (int i = 0;i<9;i++){
            hotbar.add(i, p.getInventory().getItem(i));
        }
        p.setInvulnerable(true);
        p.setCanPickupItems(false);
        p.setInvisible(invisible);
    }
    public void disable(){
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setInvisible(false);
        p.setInvulnerable(false);
        p.resetPlayerTime();
        p.resetPlayerWeather();
        for (int i = 0;i<9;i++){
            p.getInventory().setItem(i, hotbar.get(i));
        }
    }
}
