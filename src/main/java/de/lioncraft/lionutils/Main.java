package de.lioncraft.lionutils;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.MainMenu;
import de.lioncraft.lionutils.data.ChallengesData;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.commands.*;
import de.lioncraft.lionutils.commands.Status;
import de.lioncraft.lionutils.inventories.opUtils;
import de.lioncraft.lionutils.listeners.*;
import de.lioncraft.lionutils.utils.ResetUtils;
import de.lioncraft.lionutils.utils.Settings;
import de.lioncraft.lionutils.utils.status.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        plugin = this;

        this.saveDefaultConfig();
        reloadConfig();

        if(getConfig().getBoolean("delete-worlds-on-startup")){
            getLogger().info("Starting to delete Worlds");
            ResetUtils.deleteWorlds(this);
            for (ResetFunction f : list){
                f.execute();
            }
        }

        ConfigurationSerialization.registerClass(Settings.class);
        ConfigurationSerialization.registerClass(de.lioncraft.lionutils.utils.status.Status.class);
        ConfigurationSerialization.registerClass(StatusSettings.class);
        ConfigurationSerialization.registerClass(GlobalStatus.class);
        ConfigurationSerialization.registerClass(DamageDisplay.class);
        ConfigurationSerialization.registerClass(StatusPart.class);
        ConfigurationSerialization.registerClass(ChallengesData.class);

        getServer().getPluginManager().registerEvents(new StatusListeners(), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(), this);
        getServer().getPluginManager().registerEvents(new InvListeners(), this);
        getServer().getPluginManager().registerEvents(new SettingsListeners(), this);
        getServer().getPluginManager().registerEvents(new AnvilFixes(), this);
        getServer().getPluginManager().registerEvents(new DamageDisplayListeners(), this);
        getServer().getPluginManager().registerEvents(new StartupListener(), this);
        getServer().getPluginManager().registerEvents(new SharedHeartsListeners(), this);

        getCommand("ping").setExecutor(new Ping());
        getCommand("inventory").setExecutor(new Inv());
        getCommand("status").setExecutor(new Status());
        getCommand("lionsystems").setExecutor(new DefaultCommand());
        getCommand("flyspeed").setExecutor(new Flyspeed());
        getCommand("statistics").setExecutor(new StatsCommand());
        getCommand("reset").setExecutor(new ResetCommand());


        Button status = new Button(Items.get("Status", Material.NAME_TAG, "Click to configure your status."), inventoryClickEvent -> {
            Inventories.openMainMenu((Player) inventoryClickEvent.getWhoClicked());
            return false;});
        MainMenu.setButton(14, status);
        if(Bukkit.getPluginManager().isPluginEnabled("LionWaypoints")){
            Button wp = new Button(Items.get(Component.text("LionWaypoints"), Material.RECOVERY_COMPASS, "Click to open the Waypoint Menu"), inventoryClickEvent -> {
                ((Player)inventoryClickEvent.getWhoClicked()).performCommand("wp");
                return false;});

            MainMenu.setButton(16, wp);
        }
        Button openOpUtils = new Button(Items.get(Component.text("OPUtils"), Material.COMPARATOR, "Some useful stuff for Operators"), e -> {
            if (((Player)e.getWhoClicked()).isOp()) opUtils.openUI(e.getWhoClicked());
            return false;});
        MainMenu.setButton(53, openOpUtils);

    }


    private static Plugin plugin;


    @Override
    public void onDisable() {
        DamageDisplay.save();
        try {
            Settings.serializeAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                de.lioncraft.lionutils.utils.status.StatusSettings.serializeAll();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                ChallengesData.save();
            }
        }
    }

    private static List<ResetFunction> list = new ArrayList<>();
    public static void registerAfterResetAction(ResetFunction f){
        list.add(f);
    }

    @FunctionalInterface
    public interface ResetFunction {
        void execute();
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
