package de.lioncraft.lionutils.utils.spectator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lioncraft.lionapi.messageHandling.MSG;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.Main;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.UUID;

public final class SpectatorManager {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Type type = new TypeToken<SpectatorManager>(){}.getType();
    private static SpectatorManager instance;
    private HashMap<UUID, SpectatorPlayer> activeSpectators = new HashMap<>();

    public static @NotNull SpectatorPlayer getSpectatorPlayer(Player p){
        if (instance.activeSpectators.containsKey(p.getUniqueId())){
            return instance.activeSpectators.get(p.getUniqueId());
        }else{
            instance.activeSpectators.put(p.getUniqueId(),
                    new SpectatorPlayer(p));
        }
        return instance.activeSpectators.get(p.getUniqueId());
    }
    public static void saveSpectatorPlayerData(){

    }

    /**
     * Checks whether a Player has the permission to enable the spectator, switches to spectator if he does, sends an error to the player otherwise.
     * @param p The Player
     */
    public static void enableSpectator(Player p){
        if (p.hasPermission("lionutils.spectator.enable")){
            SpectatorPlayer sp = getSpectatorPlayer(p);
            sp.enable();
        }else LionChat.sendSystemMessage(MSG.NO_PERMISSION, p);
    }

    private static final File config = Main.getPlugin().getDataPath().resolve("SpectatorConfig.json").toFile();

    public static void init(){
        if (!config.exists()) {
            try {
                Files.createFile(config.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            instance = gson.fromJson(new FileReader(config), type);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(){
        try {
            Files.writeString(config.toPath(), gson.toJson(instance));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
