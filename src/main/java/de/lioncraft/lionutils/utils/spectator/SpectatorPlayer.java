package de.lioncraft.lionutils.utils.spectator;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpectatorPlayer {
    private Player p;
    private List<String> hotbar = new ArrayList<>();
    private List<byte[]> savedHotbar = new ArrayList<>();
    private boolean invisible = false;

    public SpectatorPlayer(Player p) {
        this.p = p;
    }
    public void enable(){
        p.setGameMode(GameMode.ADVENTURE);
        p.setAllowFlight(true);
        for (int i = 0;i<9;i++){
            if (p.getInventory().getItem(i) != null)
                savedHotbar.add(i, Objects.requireNonNull(p.getInventory().getItem(i)).serializeAsBytes());
            else savedHotbar.add(i, null);
        }
        p.setInvulnerable(true);
        p.setCanPickupItems(false);
        p.setAffectsSpawning(false);
        p.setSleepingIgnored(true);
        p.setInvisible(invisible);
    }
    public void disable(){
        p.setGameMode(Objects.requireNonNullElse(p.getPreviousGameMode(), GameMode.SURVIVAL));
        p.setAllowFlight(false);
        p.setAffectsSpawning(true);
        p.setSleepingIgnored(false);
        p.setFlying(false);
        p.setInvisible(false);
        p.setInvulnerable(false);
        p.resetPlayerTime();
        p.resetPlayerWeather();
        for (int i = 0;i<9;i++){
            if (savedHotbar.get(i) != null)
                p.getInventory().setItem(i, ItemStack.deserializeBytes(savedHotbar.get(i)));
            else p.getInventory().setItem(i, null);
        }
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        p.setInvisible(invisible);
    }

    public void toggleVisibility(){
        setInvisible(!isInvisible());
    }
}
