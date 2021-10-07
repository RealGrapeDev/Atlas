package me.charlotte.atlas.menu;

import com.ericstolly.menu.Menu;
import com.ericstolly.menu.button.MenuButton;
import com.ericstolly.menu.button.impl.SimpleButton;
import com.ericstolly.menu.item.ItemstackBuilder;
import com.ericstolly.menu.type.MenuType;
import me.charlotte.atlas.Atlas;
import me.charlotte.atlas.profile.Profile;
import me.charlotte.atlas.profile.UserProfile;
import me.charlotte.atlas.utils.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/6/2021 / 11:43 AM
 * Atlas / me.charlotte.atlas.menu
 */
public class ProfileLoadMenu extends Menu {

    private final Atlas atlas;
    private final UserProfile userProfile;
    private final Profile profile;

    public ProfileLoadMenu(Atlas atlas, UserProfile userProfile, Profile profile) {
        super(atlas);
        this.atlas = atlas;
        this.userProfile = userProfile;
        this.profile = profile;
    }

    @Override
    public Map<Integer, MenuButton> getButtons(Player player) {
        Map<Integer, MenuButton> buttons = new HashMap<>();
        /* New Confirm Button */
        buttons.put(3, new SimpleButton(new ItemstackBuilder(Material.EMERALD_BLOCK).name("&aLoad profile").build()
                , false
                , (InventoryClickEvent event) -> {
            /* Load the profile */
            userProfile.setActiveProfile(profile, player);
            userProfile.setupProfile(player);
            player.sendMessage(Locale.PROFILE_SELECTED.get(profile.getName()));
        }));
        /* New Delete Button */
        buttons.put(5, new SimpleButton(new ItemstackBuilder(Material.REDSTONE_BLOCK).name("&cDo not load profile").build()
                , false
                , (InventoryClickEvent event) -> player.closeInventory()));
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.GREEN + "Want to load this new profile?";
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SINGLE_ROW_CHEST;
    }
}
