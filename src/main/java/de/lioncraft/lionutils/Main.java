package de.lioncraft.lionutils;

import de.lioncraft.lionutils.data.ChallengesData;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.commands.*;
import de.lioncraft.lionutils.commands.Status;
import de.lioncraft.lionutils.listeners.*;
import de.lioncraft.lionutils.utils.ResetUtils;
import de.lioncraft.lionutils.utils.Settings;
import de.lioncraft.lionutils.utils.status.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
