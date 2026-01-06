package de.lioncraft.lionutils;

import com.comphenix.protocol.ProtocolLibrary;
import de.lioncraft.lionapi.LionAPI;
import de.lioncraft.lionapi.addons.AddonManager;
import de.lioncraft.lionapi.data.ConfigManager;
import de.lioncraft.lionapi.events.saveDataEvent;
import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Interaction.LionButtonFactory;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.MainMenu;
import de.lioncraft.lionapi.messageHandling.lang.LanguageFileManager;
import de.lioncraft.lionapi.messageHandling.lionchat.ChannelConfiguration;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionapi.velocity.connections.ConnectionManager;
import de.lioncraft.lionutils.addons.CommandUtilsAddon;
import de.lioncraft.lionutils.addons.hardcoremc.HardcoreMCAddon;
import de.lioncraft.lionutils.addons.hardcoremc.ProtocolListener;
import de.lioncraft.lionutils.data.ChallengesData;
import de.lioncraft.lionutils.inventories.DamageDisplay;
import de.lioncraft.lionutils.commands.*;
import de.lioncraft.lionutils.commands.Status;
import de.lioncraft.lionutils.inventories.opUtils;
import de.lioncraft.lionutils.listeners.*;
import de.lioncraft.lionutils.utils.GUIElementRenderer;
import de.lioncraft.lionutils.utils.ResetUtils;
import de.lioncraft.lionutils.utils.StructureUtils;
import de.lioncraft.lionutils.utils.spectator.SpectatorManager;
import de.lioncraft.lionutils.utils.status.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.saveDefaultConfig();
        reloadConfig();
        new ConfigManager(this).loadAndCheckConfig();

        LanguageFileManager.saveLangFiles(this);
        lm = LanguageFileManager.createManager(this, LionAPI.getLanguage());

        saveResourceIfNotExists("/structure.nbt", getDataPath().resolve("structure.nbt"));

        if(getConfig().getBoolean("delete-worlds-on-startup")){
            if (!getConfig().getBoolean("allow-reset")) getLogger().warning("You tried to reset the worlds but resetting is disabled. Set allow-reset to true in the plugin's config.yml to allow this.");
            else {
                getLogger().info("Starting to delete Worlds");
                ResetUtils.deleteWorlds(this);
                for (ResetFunction f : list) {
                    f.execute();
                }
            }
            Main.getPlugin().getConfig().set("delete-worlds-on-startup", false);
            getLogger().info("Finished deleting worlds");
        }


        ConfigurationSerialization.registerClass(de.lioncraft.lionutils.utils.status.Status.class);
        ConfigurationSerialization.registerClass(StatusSettings.class);
        ConfigurationSerialization.registerClass(GlobalStatus.class);
        ConfigurationSerialization.registerClass(DamageDisplay.class);
        ConfigurationSerialization.registerClass(StatusPart.class);
        ConfigurationSerialization.registerClass(ChallengesData.class);
        ConfigurationSerialization.registerClass(StructureProtectionListeners.class);

        getServer().getPluginManager().registerEvents(new StatusListeners(), this);
        getServer().getPluginManager().registerEvents(new DataListeners(), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(), this);
        getServer().getPluginManager().registerEvents(new InvListeners(), this);
        getServer().getPluginManager().registerEvents(new SettingsInvListeners(), this);
        getServer().getPluginManager().registerEvents(new AnvilFixes(), this);
        getServer().getPluginManager().registerEvents(new DamageDisplayListeners(), this);
        getServer().getPluginManager().registerEvents(new StartupListener(), this);
        getServer().getPluginManager().registerEvents(new SharedHeartsListeners(), this);
        getServer().getPluginManager().registerEvents(new LionButtonListeners(), this);

        getCommand("ping").setExecutor(new Ping());
        getCommand("inventory").setExecutor(new Inv());
        getCommand("status").setExecutor(new Status());
        getCommand("flyspeed").setExecutor(new Flyspeed());
        getCommand("statistics").setExecutor(new StatsCommand());
        getCommand("reset").setExecutor(new ResetCommand());
        getCommand("structure").setExecutor(new StructureCommand());

        AddonManager.registerAddon(CommandUtilsAddon.getInstance());
        AddonManager.registerAddon(HardcoreMCAddon.getInstance());

        SpectatorManager.init();


        MainMenu.setButton(14, LionButtonFactory.createButton(Items.get("Status", Material.NAME_TAG, "Click to configure your status."),
                "lionutils_open_status_menu"));
        MainMenu.setButton(53, LionButtonFactory.createButton(Items.get(Component.text("OPUtils"), Material.COMPARATOR, "Some useful stuff for Operators"),
                "lionutils_open_op_utils"));


        LionChat.registerChannel("status", new ChannelConfiguration(
                false,
                NamedTextColor.WHITE,
                Component.text("Status", NamedTextColor.GOLD),
                false));


        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (ConnectionManager.isConnectedToVelocity()) return;
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendPlayerListHeader(GUIElementRenderer.getHeader("Europe/Berlin"));
            }
        }, (60 - Calendar.getInstance().get(Calendar.SECOND)) * 20, 20 * 60);

        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            //ProtocolLib is only used for displaying hardcore hearts.
            //Adjust HardcoreMCAddon if you need to use ProtocolLib somewhere else
            ProtocolListener.setProtocolManager(ProtocolLibrary.getProtocolManager());
        }
    }

    @EventHandler
    public void onJoin(ServerLoadEvent e){
        StructureUtils.load();
        StructureUtils.loadStructure();
    }


    private static Plugin plugin;
    private static LanguageFileManager lm;


    @Override
    public void onDisable() {
        new DataListeners().onSave(new saveDataEvent());
    }

    public static LanguageFileManager lm() {
        return getLanguageManager();
    }

    public static LanguageFileManager getLanguageManager() {
        return lm;
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

    public static boolean saveResourceIfNotExists(String resource, Path outputPath) {
        if (resource == null || !resource.startsWith("/")) {
            System.err.println("Error: Resource path must be a non-empty absolute path starting with '/' (e.g., /com/example/file.txt).");
            return false;
        }

        File targetFile = outputPath.toFile();

        if (targetFile.exists()) {
            if (targetFile.isDirectory()) {
                return false;
            }
            return false;
        }
        try {
            InputStream resourceStream = Main.class.getResourceAsStream(resource);
            {
                if (resourceStream == null) {
                    return false;
                }
                Path parentDir = outputPath.getParent();
                if (parentDir != null) {
                    if (!Files.exists(parentDir)) {
                        Files.createDirectories(parentDir);
                    }
                }

                Files.copy(resourceStream, outputPath);
                return true;
            }
        }catch(IOException e) {
            System.err.println("Error saving resource '" + resource + "' to '" + outputPath + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (SecurityException e) {
            System.err.println("Error creating directories for '" + outputPath + "' due to security restrictions: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
