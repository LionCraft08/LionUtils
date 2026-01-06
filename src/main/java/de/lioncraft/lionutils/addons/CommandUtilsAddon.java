package de.lioncraft.lionutils.addons;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import de.lioncraft.lionapi.addons.AbstractAddon;
import de.lioncraft.lionapi.data.BasicSettings;
import de.lioncraft.lionapi.data.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CommandUtilsAddon extends AbstractAddon {
    static final File filePath = new File(Main.getPlugin().getDataFolder(), "addons\\commandutils_addon.yml");
    static final File legacyFilePath = new File(Main.getPlugin().getDataFolder(), "commandutils_addon.yml");

    private static final CommandUtilsAddon instance = new CommandUtilsAddon();
    private final BasicSettings settings;

    public CommandUtilsAddon() {
        super("command_utils", MiniMessage.miniMessage().deserialize("<gradient:#228B22:#9ACD32>CommandUtils"));
        super.setUseOwnChannel(true);
        if (!filePath.exists()){
            filePath.getParentFile().mkdirs();
            try {
                if (legacyFilePath.exists()){
                    Files.copy(legacyFilePath.toPath(), filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }else filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (legacyFilePath.exists()){
            legacyFilePath.delete();
        }
        settings = new BasicSettings(YamlConfiguration.loadConfiguration(filePath),
                new ArrayList<>(List.of(
                        new Setting<Boolean>("enabled"),
                        new Setting<>("enderchest_enabled",
                                List.of(get("<white>Enables the Enderchest command to allow"),
                                        get("<white>Players to open their ec from anywhere")))
                                .setValue(true),
                        new Setting<>("crafting_table_enabled",
                                List.of(get("<white>Enables the Crafting Table Command so"),
                                        get("<white>placing one isn't necessary any more")))
                                .setValue(true),
                        new Setting<>("furnace_command",
                                List.of(get("<white>Enables the Furnace Command that allows"),
                                        get("<white>Players to have a mobile furnace")))
                                .setValue(false),
                        new Setting<>("brewing_stand_enabled",
                                List.of(get("<white>Enables a Brewing Stand that allows"),
                                        get("<white>Players to have their mobile brewing station")))
                                .setValue(false),
                        new Setting<>("anvil_command",
                                List.of(get("<white>Enables the Anvil Command that allows"),
                                        get("<white>use a mobile anvil (this command is even"),
                                        get("<white>more cheating, as Players don't need any iron)")))
                                .setValue(false),
                        new Setting<>("tool_blocks_enabled",
                                List.of(get("<white>Enables the Tools Command that can"),
                                        get("<white>open any other utility block like Smithing Table, ...")))
                                .setValue(false)
                )));
    }

    public static CommandUtilsAddon getInstance() {
        return instance;
    }

    public BasicSettings getSettings() {
        return settings;
    }

    @Override
    protected void onLoad() {
        registerEvent(new Listener() {
            @EventHandler(priority = EventPriority.HIGH)
            public void onCommandArgsSend(PlayerCommandSendEvent e){
                e.getCommands().add("enderchest");
            }
        });
    }

    @Override
    protected void onUnload() {
        unloadEvents();
    }

    @Override
    public ItemStack getSettingsIcon(){
        return Items.get("commandUtils", Material.COMMAND_BLOCK);
    }

    private static Component get(String miniMessage){
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }

    public static void save(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(filePath);
        getInstance().settings.saveTo(config);
        try {
            config.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
