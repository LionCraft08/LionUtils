package de.lioncraft.lionutils.utils.status;

import java.util.HashMap;
import java.util.Map;

public class CustomStatusManager {
    private static HashMap<String, GlobalStatus> customStatusMap = new HashMap<>(
            Map.of("team", new TeamStatus(null))
    );
    public static Status getCustomStatus(String name){
        return customStatusMap.get(name);
    }

    public static void addCustomStatus(String s, GlobalStatus customStatus) {
        CustomStatusManager.customStatusMap.put(s, customStatus);
    }

    public static HashMap<String, GlobalStatus> getCustomStatusMap() {
        return customStatusMap;
    }
}
