package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.messageHandling.ColorGradient;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StatusPart implements ConfigurationSerializable {
    private String content;
    private TextColor color, gradiantColor;
    private boolean hasGradiant;
    private TextComponent tc;

    public StatusPart(TextComponent c){
        content = c.content();
        color = c.color();
        gradiantColor = TextColor.color(255, 255, 255);
        hasGradiant = false;
        if(color == null){
            color = TextColor.color(255, 255, 255);
        }
        buildComponent();
    }

    public StatusPart(String content){
        this.content = content;
        color = TextColor.color(255, 255, 255);
        gradiantColor = TextColor.color(255, 255, 255);
        hasGradiant = false;
        buildComponent();
    }

    public @NotNull TextComponent getComponent(){
        if(tc == null) buildComponent();
        return tc;
    }

    public String getContent() {
        return content;
    }

    public TextColor getColor() {
        return color;
    }

    public TextColor getGradiantColor() {
        return gradiantColor;
    }

    public boolean hasGradiant() {
        return hasGradiant;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
        buildComponent();
    }

    public void setHasGradiant(boolean hasGradiant) {
        this.hasGradiant = hasGradiant;
        buildComponent();
    }

    public void setGradiantColor(@NotNull TextColor gradiantColor) {
        this.gradiantColor = gradiantColor;
        buildComponent();
    }

    public void setColor(@NotNull TextColor color) {
        this.color = color;
        buildComponent();
    }

    private void buildComponent(){
        if(hasGradiant){
            tc = (TextComponent) ColorGradient.getNewGradiant(content, color, gradiantColor);
        }else tc = Component.text(content, color);
    }

    public StatusPart(Map<String, Object> map){
        content = (String) map.get("content");
        color = TextColor.fromHexString((String) map.get("color"));
        gradiantColor = TextColor.fromHexString((String) map.get("gradiantColor"));
        hasGradiant = (boolean) map.get("hasGradiant");
    }
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("hasGradiant", hasGradiant);
        map.put("color", color.asHexString());
        map.put("gradiantColor", gradiantColor.asHexString());
        return map;
    }
}
