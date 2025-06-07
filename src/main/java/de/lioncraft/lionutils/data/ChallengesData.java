package de.lioncraft.lionutils.data;

import de.lioncraft.lionutils.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class ChallengesData implements ConfigurationSerializable {
    private static ChallengesData instance;
    public static ChallengesData getInstance(){
        if (instance == null) instance = loadData();
        return instance;
    }
    private boolean sharedHearts, globalShared;

    public ChallengesData() {
        sharedHearts = false;
    }

    public boolean isSharedHearts() {
        return sharedHearts;
    }

    public void setSharedHearts(boolean sharedHearts) {
        this.sharedHearts = sharedHearts;
    }

    public boolean isGlobalShared() {
        return globalShared;
    }

    public void setGlobalShared(boolean globalShared) {
        this.globalShared = globalShared;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("shared-hearts", sharedHearts, "global-shared", globalShared);

    }
    public ChallengesData(Map<String, Object> map){
        sharedHearts = (boolean) map.get("shared-hearts");
        globalShared = (boolean) map.get("global-shared");
    }
    public static void save(){
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(getFile());
        yml.set("config", instance);
        try {
            yml.save(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static File getFile(){
        File f = new File(Main.getPlugin().getDataFolder(), "ChallengesData.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return f;
    }
    public static ChallengesData loadData(){
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(getFile());
        if (yml.contains("config")) return (ChallengesData) yml.get("config");
        else return new ChallengesData();
    }
}
