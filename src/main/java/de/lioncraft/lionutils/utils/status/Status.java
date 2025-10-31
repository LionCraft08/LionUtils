package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Interaction.LionButtonFactory;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Status implements ConfigurationSerializable {
    private List<StatusPart> list;
    private String name;
    private boolean showInPlayerListOnly;
    private TextColor nameColor;
    private UUID playerID;
    private Material material;

    public OfflinePlayer getPlayer(){
        if(playerID == null){
            return null;
        }
        return Bukkit.getOfflinePlayer(playerID);
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status(@Nullable List parts, OfflinePlayer player, boolean showInPlayerListOnly, TextColor color){
        list = new ArrayList<>();
        if(parts != null){
            if(!parts.isEmpty()){
                if(parts.get(0) instanceof TextComponent){
                    for(Object c : parts){
                        list.add(new StatusPart((TextComponent) c));
                    }
                }else{
                    for(Object c : parts){
                        list.add((StatusPart) c);
                    }
                }

            }
        }
        buildStatus(list, player, showInPlayerListOnly, color);

    }

    private void buildStatus(@Nullable List<StatusPart> parts, OfflinePlayer player, boolean showInPlayerListOnly, TextColor color){
        list = new ArrayList<>();
        if(parts != null) {
            list = parts;
            if (!list.isEmpty())
                name = getRawContent();
        }
        if(player != null){
            this.playerID = player.getUniqueId();
        }
        this.showInPlayerListOnly = showInPlayerListOnly;
        this.nameColor = color;
        material = Material.NAME_TAG;
        if (name == null) name = "mystatus_0";
    }

    public Status(@Nullable List<TextComponent> parts, @Nullable OfflinePlayer player, boolean showInPlayerListOnly, TextColor color, Material material){
        List<StatusPart> list = new ArrayList<>();
        if(parts != null){
            for(TextComponent c : parts){
                list.add(new StatusPart(c));
            }
        }
        buildStatus(list, player, showInPlayerListOnly, color);
        this.material = material;
    }
    public void setMaterial(Material m){
        if(m.isItem()){
            material = m;
        }
    }
    public Material getMaterial(){
        if(material == null){
            material = Material.NAME_TAG;
        }
        return material;
    }
    public boolean removePart(int part){
        if(list.size()>part){
            list.remove(part);
            update();
            return true;
        } else return false;
    }

    public void addPart(@NotNull TextComponent part){
        list.add(new StatusPart(part));
        update();
    }

    /**Clones the Status to be added to a new Player's created statuses
     * @param p the Player to clone the status to
     * @return the newly created Status
     */
    public Status attachToPlayer(OfflinePlayer p){
        Status s = new Status(list, p, showInPlayerListOnly, nameColor);
        s.setName(getName());
        return s;
    }

    public Component getAsComponent(String playerName){
        TextComponent c = Component.text("[", TextColor.color(128, 128, 128));
        for(StatusPart component : list){
            c = c.append(component.getComponent());
        }
        c = c.append(Component.text("] ", TextColor.color(128, 128, 128)));
        if(playerName != null){
            c = c.append(Component.text(playerName, nameColor));
        }
        return c;
    }
    public Component getAsComponent(){
        if(playerID == null){
            return getAsComponent(null);
        }
        return getAsComponent(getPlayer().getName());
    }
    public void setList(List<StatusPart> list) {
        this.list = list;
        update();
    }
    public void setText(int index, String content){
        list.get(index).setContent(content);
        update();
    }
    public void setColor(int part, TextColor color){
        list.get(part).setColor(color);
        update();
    }
    public void setText(int index, TextComponent content){
        list.set(index, new StatusPart(content));
        update();
    }
    public List<StatusPart> getList() {
        return list;
    }
    public TextColor getNameColor() {
        return nameColor;
    }
    public void setNameColor(TextColor nameColor) {
        this.nameColor = nameColor;
        update();
    }
    public boolean isShowInPlayerListOnly() {
        return showInPlayerListOnly;
    }
    public void setShowInPlayerListOnly(boolean showInPlayerListOnly) {
        this.showInPlayerListOnly = showInPlayerListOnly;
        update();
    }
    public ItemStack getItem(int part){
        ItemStack is = Items.get(list.get(part).getComponent(), Material.BAMBOO_HANGING_SIGN, "RIGHTCLICK to Configure", "DROP KEY to delete");
        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(StatusListeners.disabledClickItems, PersistentDataType.BOOLEAN, true);
        is.setItemMeta(im);
        Button b = new Button(is, event -> {
            if(event.getClick().equals(ClickType.DROP)) {
                list.remove(part);
                if(StatusSettings.getSettings((Player) event.getWhoClicked()).getCurrentStatus().equals(this)){
                    StatusSettings.getSettings((Player) event.getWhoClicked()).getCurrentStatus().update();
                }
                Inventories.openConfigureStatusMenu(event.getWhoClicked(), this);
            } else if(event.isLeftClick()|| event.isRightClick()) {
                Inventories.openConfigurePartMenu(event.getWhoClicked(), part, this);
            } else return false;
        return true;});
        return b.getButton();
    }
    public ItemStack getItem(String statusType) {
        ItemStack is = LionButtonFactory.createButton(
                Items.get(getAsComponent(), getMaterial(), TextColor.color(nameColor),
                        "- Leftclick to select", "- Rightclick to configure", "- Drop Key to delete"),
                "lionutils_statusgui_status."+statusType+":"+getName()
                );
        if (StatusSettings.getSettings(getPlayer()) != null) {
            if (StatusSettings.getSettings(getPlayer()).isCurrentStatus(getName())) ;
            {
                ItemMeta im = is.getItemMeta();
                im.setEnchantmentGlintOverride(true);
                is.setItemMeta(im);
            }
        }
//        Button b = new Button(is, event -> {
//            if (event.getClick().equals(ClickType.DROP)){
//                StatusSettings.getSettings((Player)event.getWhoClicked()).removeStatus(this);
//                new openStatusConfigureGUILater((Player) event.getWhoClicked(), -1, this).runTaskLater(Main.getPlugin(), 1);
//            }else if(event.isLeftClick()){
//                StatusSettings.getSettings((Player) event.getWhoClicked()).setCurrentStatus(this);
//                new openStatusConfigureGUILater((Player) event.getWhoClicked(), -1, this).runTaskLater(Main.getPlugin(), 1);
//            } else if (event.isRightClick()){
//                if(event.getWhoClicked() instanceof Player p){
//                    if (this instanceof TeamStatus) return false;
//                    new openStatusConfigureGUILater(p, -2, this).runTaskLater(Main.getPlugin(), 1);
//                }else return false;
//            }else return false;
//            return true;});
        return is;
    }

    public void update(){
        if (getPlayer() != null){
            if (getPlayer().isOnline()){
                update(getPlayer().getPlayer());
            }
        }
    }

    public void update(Player player){
        if(player != null){
            if(!showInPlayerListOnly){
                player.displayName(getAsComponent(player.getName()));
            }else player.displayName(player.name());
            player.playerListName(getAsComponent(player.getName()));
        }
    }

    public String getRawContent(int part){
        return list.get(part).getContent();
    }
    public String getRawContent(){
        if (list.isEmpty()) return "";
        return list.stream().map(StatusPart::getContent).collect(Collectors.joining());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> m = new HashMap<>();
        m.put("newcontent", list);
        m.put("listonly", showInPlayerListOnly);
        if(getPlayer() != null){
            m.put("player", getPlayer().getUniqueId().toString());
        }else m.put("player", null);
        m.put("namecolor", nameColor.asHexString());
        m.put("name", name);
        m.put("material", getMaterial().toString());
        return m;
    }
    public Status(Map<String, Object> data){
        list = new ArrayList<>();
        if(data.containsKey("content")){
            List<String> content = (List<String>) data.get("content");
            List<TextComponent> list2 = new ArrayList<>();
            for(String s : content){
                String hex = s.substring(s.lastIndexOf(":")+1);
                list2.add(Component.text(s.substring(0, s.lastIndexOf(":")), TextColor.fromHexString(hex)));
            }
            list2.forEach(textComponent -> list.add(new StatusPart(textComponent)));
        }else list = (List<StatusPart>) data.get("newcontent");
        showInPlayerListOnly = (boolean) data.get("listonly");
        if(data.get("player") != null){
            playerID = UUID.fromString((String) data.get("player"));
        }
        name = (String) data.get("name");
        if (name == null) name = getContent();
        if (name == null || name.isBlank()) name = "mystatus_0";
        nameColor = TextColor.fromHexString((String) data.get("namecolor"));
        material = Material.valueOf((String) data.get("material"));

    }
    public String getContent(){
        StringBuilder s = new StringBuilder();
        for(StatusPart c : list){
            s.append(c.getContent());
        }
        return s.toString();
    }
}
