package de.lioncraft.lionutils.utils;

import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.listeners.StructureProtectionListeners;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class StructureUtils
{
    private StructureUtils(){}
    private static File structureFile = Main.getPlugin().getDataPath().resolve("structure.nbt").toFile();
    private static Structure structure;

    static {
        try {
            structure = Bukkit.getStructureManager().loadStructure(structureFile);
        } catch (IOException e) {
            LionChat.sendLogMessage("Couldn't load structure!");
            throw new RuntimeException(e);
        }

    }

    private static File dataFile = Main.getPlugin().getDataPath().resolve("structure_config.yml").toFile();

    public static void save(){
        if(!dataFile.exists()){
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("structures", StructureProtectionListeners.getActiveListeners());
        config.set("spawnLocation", StructureProtectionListeners.respawnLocation);
        try {
            config.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void load(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        if(config.contains("spawnLocation")){
            StructureProtectionListeners.respawnLocation = config.getLocation("spawnLocation");
        }
        if(config.contains("structures")) {
            Object o = config.get("structures");
            if(o != null){
                StructureProtectionListeners.setStructureProtectionListeners((List<StructureProtectionListeners>) o);
            }
        }
    }
    public static void createStructure(Location center, StructureRotation rotation){
        Location corner = getCenteredOrigin(center, rotation, structure);
        World world = corner.getWorld();
        boolean previousValue = Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_TILE_DROPS));

        // Disable drops
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        structure.place(corner, true, rotation, Mirror.NONE, 0, 1.0f, new Random());

        world.setGameRule(GameRule.DO_TILE_DROPS, previousValue);
    }

    public static void protectStructure(Structure structure, Location corner, StructureRotation rotation){
        Location loc2 = getCorner2(rotation, corner, structure);
        Bukkit.getPluginManager().registerEvents(
                new StructureProtectionListeners(
                        corner.toVector(),
                        loc2.toVector(),
                        corner.getWorld().getName()),
                Main.getPlugin());
    }

    public static void setWorldSpawn(Location location){
        location.getWorld().setSpawnLocation(location);
    }

    public static void loadStructure(){
        structure.getSize();
    }

    /**
     * Creates the Structure, Changes the world spawn, protects it from being destroyed and tp's players
     */
    public static void doEverything(Player player){
        doEverything(player.getLocation(), getRotationFromPlayer(player));
    }
    /**
     * Creates the Structure, Changes the world spawn, protects it from being destroyed and tp's players
     */
    public static void doEverything(Location location, StructureRotation rotation){
        createStructure(location, rotation);
        protectStructure(structure, getCenteredOrigin(location, rotation, structure), rotation);
        setWorldSpawn(location);
        StructureProtectionListeners.respawnLocation = location;
        Bukkit.getWorld("world").getSpawnLocation().toString();
    }

    /**
     * Calculates the origin point so the player is in the X/Z center.
     */
    public static Location getCenteredOrigin(Location location, StructureRotation rotation, Structure structure) {
        Vector size = structure.getSize();

        double xOffset;
        double zOffset;

        // Adjust offsets based on rotation because width and length swap at 90/270 degrees
        if (rotation == StructureRotation.CLOCKWISE_90 || rotation == StructureRotation.COUNTERCLOCKWISE_90) {
            xOffset = size.getZ() / 2.0;
            zOffset = size.getX() / 2.0;
        } else {
            xOffset = size.getX() / 2.0;
            zOffset = size.getZ() / 2.0;
        }

        // Subtract the offsets from the player's current location
        return switch (rotation) {
            case NONE -> location.clone().subtract(xOffset, 3, zOffset);
            case CLOCKWISE_180 ->  location.clone().add(xOffset, -3, zOffset);
            case COUNTERCLOCKWISE_90 ->   location.clone().subtract(xOffset, 3, -zOffset);
            case CLOCKWISE_90 ->   location.clone().subtract(-xOffset, 3, zOffset);
        };
    }

    public static Location getCorner2(StructureRotation rotation, Location corner1, Structure structure) {
        Vector size = structure.getSize();

        double xOffset;
        double zOffset;

        // Adjust offsets based on rotation because width and length swap at 90/270 degrees
        if (rotation == StructureRotation.CLOCKWISE_90 || rotation == StructureRotation.COUNTERCLOCKWISE_90) {
            xOffset = size.getZ();
            zOffset = size.getX();
        } else {
            xOffset = size.getX();
            zOffset = size.getZ();
        }


        // Subtract the offsets from the player's current location
        return switch (rotation) {
            case NONE -> corner1.clone().add(xOffset, size.getY(), zOffset);
            case CLOCKWISE_180 ->  corner1.clone().subtract(xOffset, 0, zOffset).add(0, size.getY(), 0);
            case COUNTERCLOCKWISE_90 ->   corner1.clone().add(xOffset, size.getY(), -zOffset);
            case CLOCKWISE_90 ->   corner1.clone().add(-xOffset, size.getY(), zOffset);
        };
    }

    public static void setEntrance(boolean open){
        for (StructureProtectionListeners spl : StructureProtectionListeners.getActiveListeners()){
            World w = Bukkit.getWorld(spl.getWorldName());
            Location l1 = spl.getPos1().toLocation(w).toBlockLocation();
            Location l2 = spl.getPos2().toLocation(w).toBlockLocation();
            for (int i = l1.getBlockX(); i <= l2.getBlockX(); i++) {
                for (int j = l1.getBlockY(); j <= l2.getBlockY(); j++) {
                    for (int k = l1.getBlockZ(); k <= l2.getBlockZ(); k++) {
                        Block b = Bukkit.getWorld(spl.getWorldName()).getBlockAt(i, j, k);
                        Material m = b.getType();
                        if (m.equals(Material.LIGHT) && !open) b.setType(Material.BARRIER);
                        else if (m.equals(Material.BARRIER) && open) b.setType(Material.LIGHT);
                    }
                }
            }
        }
    }

    /**
     * Calculates the StructureRotation based on the player's facing direction.
     * This ensures the structure "faces" the way the player is looking.
     */
    public static StructureRotation getRotationFromPlayer(Player player) {
        float yaw = player.getLocation().getYaw();

        // Normalize yaw to 0-360 range
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 315 || yaw < 45) {
            return StructureRotation.NONE; // South
        } else if (yaw >= 45 && yaw < 135) {
            return StructureRotation.CLOCKWISE_90; // West
        } else if (yaw >= 135 && yaw < 225) {
            return StructureRotation.CLOCKWISE_180; // North
        } else if (yaw >= 225 && yaw < 315) {
            return StructureRotation.COUNTERCLOCKWISE_90; // East
        }

        return StructureRotation.NONE;
    }
}
