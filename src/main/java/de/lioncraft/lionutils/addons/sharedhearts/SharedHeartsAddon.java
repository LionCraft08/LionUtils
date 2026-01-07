package de.lioncraft.lionutils.addons.sharedhearts;

import de.lioncraft.lionapi.addons.AbstractAddon;
import de.lioncraft.lionapi.data.BasicSettings;
import de.lioncraft.lionapi.data.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.teams.Team;
import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.addons.hardcoremc.HardcoreMCAddon;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SharedHeartsAddon extends AbstractAddon {
    static final File filePath = new File(Main.getPlugin().getDataFolder(), "addons\\sharedhearts_addon.yml");

    public static void save(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(filePath);
        getInstance().settings.saveTo(config);
        try {
            config.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SharedHeartsAddon instance = new SharedHeartsAddon();

    public static SharedHeartsAddon getInstance() {
        return instance;
    }

    private final BasicSettings settings;

    public BasicSettings getSettings() {
        return settings;
    }

    public Collection<? extends Player> getAffectedPlayers(Player damageSource){
        if(!this.isEnabled()) return new ArrayList<>();
        if (SharedHeartsAddon.getInstance().getSettings().getBoolValue("force-global"))
            return Bukkit.getOnlinePlayers();
        else {
            Team t = Team.getTeam(damageSource);
            if (t == null) return new ArrayList<>();
            else return t.getOnlinePlayers();
        }
    }

    public SharedHeartsAddon() {
        super("shared_hearts", Main.lm().msg("inv.sharedhearts.name"));
        super.setUseOwnChannel(false);

        if (!filePath.exists()){
            filePath.getParentFile().mkdirs();
            try {
                filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.settings = new BasicSettings(YamlConfiguration.loadConfiguration(filePath),
                new ArrayList<>(List.of(
                        new Setting<>("force-global", false),
                        new Setting<>("sync-damage-only", false)
                )));
    }

    @Override
    protected void onLoad() {
        registerEvent(new SharedHeartsListeners());
    }

    @Override
    protected void onUnload() {
        unloadEvents();
    }
    @Override
    public ItemStack getSettingsIcon(){
        return Items.get(Main.lm().msg("inv.sharedhearts.name"), Material.LEAD, Main.lm().msg("inv.sharedhearts.lore"));
    }

    @Override
    public void openConfigMenu(Player player){
        SharedHeartsConfigUI.getInstance().openConfigMenu(player);
    }
}
