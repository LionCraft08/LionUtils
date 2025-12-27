package de.lioncraft.lionutils.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StructureProtectionListeners implements Listener {
    private static List<StructureProtectionListeners> activeListeners = new ArrayList<>();

    public static List<StructureProtectionListeners> getActiveListeners() {
        return activeListeners;
    }

    private final Vector pos1;
    private final Vector pos2;
    private final String worldName;
    public static Location respawnLocation;

    public StructureProtectionListeners(Vector pos1, Vector pos2, String worldName) {
        this.pos1 = Vector.getMinimum(pos1, pos2);
        this.pos2 = Vector.getMaximum(pos1, pos2);
        this.worldName = worldName;
        activeListeners.add(this);

        LionChat.sendDebugMessage("Protecting Area: "+pos1 +" - "+pos2.toString());
    }

    public Vector getPos1() {
        return pos1;
    }

    public Vector getPos2() {
        return pos2;
    }

    public String getWorldName() {
        return worldName;
    }

    private boolean isWithinProtectedArea(Location loc) {
        if (!loc.getWorld().getName().equals(worldName)) return false;
        return loc.toVector().isInAABB(pos1, pos2);
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (respawnLocation == null) {return;}
        e.setRespawnLocation(respawnLocation);
    }

    // --- PLAYER & WORLD INTERACTION ---

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    // --- PHYSICS & NATURAL CHANGES ---

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) { // Ice melting, coral dying, snow melting
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) { // Fire destroying blocks
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) { // Fire spreading, mushrooms growing
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) { // Crops/Trees growing
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) { // Concrete hardening, obsidian forming
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    // --- EXPLOSIONS ---

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> isWithinProtectedArea(block.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) { // Bed/Respawn Anchor explosions
        event.blockList().removeIf(block -> isWithinProtectedArea(block.getLocation()));
    }

    // --- ENTITY ACTIONS ---

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) { // Endermen, Wither, Falling Sand
        if (isWithinProtectedArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    // --- PISTONS ---

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isWithinProtectedArea(block.getLocation()) ||
                    isWithinProtectedArea(block.getRelative(event.getDirection()).getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isWithinProtectedArea(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
