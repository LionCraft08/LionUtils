package de.lioncraft.lionutils;

import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.inventories.MainGUI;
import de.lioncraft.lionutils.inventories.PlayerSettingsGUI;
import de.lioncraft.lionutils.commands.*;
import de.lioncraft.lionutils.commands.Status;
import de.lioncraft.lionutils.listeners.*;
import de.lioncraft.lionutils.utils.InvWatcher;
import de.lioncraft.lionutils.utils.Settings;
import de.lioncraft.lionutils.utils.status.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        plugin = this;
        ConfigurationSerialization.registerClass(Settings.class);
        ConfigurationSerialization.registerClass(de.lioncraft.lionutils.utils.status.Status.class);
        ConfigurationSerialization.registerClass(StatusSettings.class);
        ConfigurationSerialization.registerClass(GlobalStatus.class);
        ConfigurationSerialization.registerClass(DamageDisplay.class);
        ConfigurationSerialization.registerClass(StatusPart.class);

        this.saveDefaultConfig();

        InvWatcher.Initialize();
        PlayerSettingsGUI.Initialize();
        MainGUI.Initialize();
        StatusChecker.thisTask = new StatusChecker().runTaskTimer(this, StatusChecker.afkTime, 200);

        getServer().getPluginManager().registerEvents(new StatusListeners(), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(), this);
        getServer().getPluginManager().registerEvents(new InvListeners(), this);
        getServer().getPluginManager().registerEvents(new SettingsListeners(), this);
        getServer().getPluginManager().registerEvents(new AnvilFixes(), this);
        getServer().getPluginManager().registerEvents(new DamageDisplayListeners(), this);

        getCommand("ping").setExecutor(new Ping());
        getCommand("inventory").setExecutor(new Inv());
        getCommand("status").setExecutor(new Status());
        getCommand("lionsystems").setExecutor(new DefaultCommand());
        getCommand("flyspeed").setExecutor(new Flyspeed());
        getCommand("statistics").setExecutor(new StatsCommand());

        Settings.deserializeAll();
        de.lioncraft.lionutils.utils.status.StatusSettings.deserializeAll();
        DamageDisplay.deserialize();
        DamageDisplay.Init();
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
            }

        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
