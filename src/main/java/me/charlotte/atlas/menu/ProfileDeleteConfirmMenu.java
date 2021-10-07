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
 * 10/6/2021 / 1:57 PM
 * Atlas / me.charlotte.atlas.menu
 */
public class ProfileDeleteConfirmMenu extends Menu {

    private final Atlas atlas;
    private final UserProfile userProfile;
    private final Profile profile;

    public ProfileDeleteConfirmMenu(Atlas atlas, UserProfile userProfile, Profile profile) {
        super(atlas);
        this.atlas = atlas;
        this.userProfile = userProfile;
        this.profile = profile;
    }

    @Override
    public Map<Integer, MenuButton> getButtons(Player player) {
        Map<Integer, MenuButton> buttons = new HashMap<>();
        buttons.put(3, new SimpleButton(new ItemstackBuilder(Material.EMERALD_BLOCK).name("&aYes").build(), false, (InventoryClickEvent event) -> {
            /* We run the save method on an executor service since it iterates through all of the profiles and may cause lag. */
            atlas.getExecutorService().execute(() -> {
                userProfile.getAllProfiles().remove(profile);
                userProfile.save();
            });
            player.closeInventory();
            player.sendMessage(Locale.PROFILE_DELETED.get(profile.getName()));
        }));
        buttons.put(5, new SimpleButton(new ItemstackBuilder(Material.REDSTONE_BLOCK).name("&cNo").build(), false, (InventoryClickEvent event) -> {
            player.closeInventory();
            player.sendMessage(Locale.PROFILE_DELETE_CANCELLED.get());
        }));
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Confirm Delete " + ChatColor.WHITE + profile.getName();
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SINGLE_ROW_CHEST;
    }
}
