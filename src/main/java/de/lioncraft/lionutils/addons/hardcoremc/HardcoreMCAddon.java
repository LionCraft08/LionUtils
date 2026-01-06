package de.lioncraft.lionutils.addons.hardcoremc;

import de.lioncraft.lionapi.addons.AbstractAddon;
import de.lioncraft.lionapi.data.BasicSettings;
import de.lioncraft.lionapi.data.Setting;
import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HardcoreMCAddon extends AbstractAddon {
    static final File filePath = new File(Main.getPlugin().getDataFolder(), "addons\\hardcoremc_addon.yml");

    public static void save(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(filePath);
        getInstance().settings.saveTo(config);
        try {
            config.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HardcoreMCAddon instance = new HardcoreMCAddon();

    public static HardcoreMCAddon getInstance() {
        return instance;
    }

    private final BasicSettings settings;

    private HardcoreMCListener listener;

    private HardcoreMCAddon() {
        super("hardcore_mc", MiniMessage.miniMessage().deserialize("<#FF5500>Hardcore MC"));
        super.setUseOwnChannel(false);

        if (!filePath.exists()){
            filePath.getParentFile().mkdirs();
            try {
                filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        settings = new BasicSettings(YamlConfiguration.loadConfiguration(filePath), new ArrayList<>(
                List.of(
                        new Setting<>("regeneration_mode", "NORMAL"),
                        new Setting<>("show_hardcore_hearts",
                                false
                        ).setOnChange((oldValue, newValue) -> {
                            if (newValue != oldValue && isProtocolLibEnabled()) {
                                if (newValue) {
                                    ProtocolLibImpl.registerNewListener();
                                }else{
                                    //Removes every listener. Might need to remove that when further using ProtocolLib
                                    ProtocolLibImpl.removeListeners(Main.getPlugin());
                                }
                            }
                            return newValue;
                        }),
                        new Setting<>("half_heart", false)
                )
        ));
        listener = new HardcoreMCListener();
    }

    public static boolean isProtocolLibEnabled(){
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public BasicSettings getSettings() {
        return settings;
    }

    @Override
    public ItemStack getSettingsIcon(){
        return HardcoreMCConfigUI.getInstance().getButton();
    }

    @Override
    public void openConfigMenu(Player player){
        player.openInventory(HardcoreMCConfigUI.getInstance().getConfigInv());
    }

    @Override
    protected void onLoad() {
        super.registerEvent(new HardcoreMCListener());
    }

    @Override
    protected void onUnload() {
        super.unloadEvents();
        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getAttribute(Attribute.MAX_HEALTH) == null)
                p.registerAttribute(Attribute.MAX_HEALTH);
            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        }
    }
}
