package me.charlotte.atlas.menu;

import com.ericstolly.menu.Menu;
import com.ericstolly.menu.button.MenuButton;
import com.ericstolly.menu.button.impl.SimpleButton;
import com.ericstolly.menu.item.ItemstackBuilder;
import com.ericstolly.menu.type.MenuType;
import me.charlotte.atlas.Atlas;
import me.charlotte.atlas.listeners.ProfileListener;
import me.charlotte.atlas.profile.Profile;
import me.charlotte.atlas.profile.UserProfile;
import me.charlotte.atlas.utils.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/5/2021 / 10:34 PM
 * Atlas / me.charlotte.atlas.menu
 */
public class ProfileMenu extends Menu {

    private final UserProfile userProfile;
    private final Atlas atlas;

    public ProfileMenu(Atlas atlas, UserProfile userProfile) {
        super(atlas);
        this.atlas = atlas;
        this.userProfile = userProfile;
    }

    @Override
    public Map<Integer, MenuButton> getButtons(Player player) {
        Map<Integer, MenuButton> buttons = new HashMap<>();

        /* We iterate through all of the Profiles the UserProfile has, and create a new button with the profile's data */
        for (Profile profile : userProfile.getAllProfiles()) {
            List<String> lore = atlas.getConfig().getStringList("PROFILES-MENU.PROFILE-BUTTON.LORE");
            List<String> newLore = new ArrayList<>();
            for (String s : lore) {
                newLore.add(s
                        .replace("{name}", profile.getName())
                        .replace("{world}", profile.getLocation().getWorld().getName())
                        .replace("{x}", profile.getLocation().getX() + "")
                        .replace("{y}", profile.getLocation().getY() + "")
                        .replace("{z}", profile.getLocation().getZ() + "")
                        .replace("{health}", profile.getHealthLevel() + "")
                        .replace("{gamemode}", profile.getGameMode().toString())
                        .replace("{selected}", userProfile.getSelectedProfile().getName().equalsIgnoreCase(profile.getName())
                                ? ChatColor.GREEN + "True"
                                : ChatColor.RED + "False"));
            }
            buttons.put(buttons.size(), new SimpleButton(
                    new ItemstackBuilder(Material.valueOf(atlas.getConfig().getString("PROFILES-MENU.PROFILE-BUTTON.MATERIAL"))).name(atlas.getConfig().getString("PROFILES-MENU.PROFILE-BUTTON.NAME"))
                            .lore(newLore).build(),
                    false,
                    (InventoryClickEvent event) -> {
                        /* Check if Click is Left-Click */
                        if (event.getClick().isLeftClick()) {
                            /* Check if Click is Shift-Click */
                            if (event.getClick().isShiftClick()) {
                                /* Puts them in the rename profile procedure map */
                                ProfileListener.getRenameProfileMap().put(player.getUniqueId(), profile);
                                player.sendMessage(Locale.PROFILE_RENAME_PROCEDURE.get(profile.getName()));
                                player.closeInventory();
                                return;
                            }
                            /* Checks if the UserProfile already has the profile they clicked selected */
                            if (userProfile.getSelectedProfile().getName().equalsIgnoreCase(profile.getName())) {
                                player.sendMessage(Locale.PROFILE_ALREADY_SELECTED.get());
                                return;
                            }
                            /* We set their active profile and reset up their attributes */
                            userProfile.setActiveProfile(profile, player);
                            userProfile.setupProfile(player);
                            player.sendMessage(Locale.PROFILE_SELECTED.get(profile.getName()));
                        }
                        /* Check if Click is Right-Click */
                        if (event.getClick().isRightClick()) {
                            /* Checks if the UserProfile already has the profile they clicked selected */
                            if (userProfile.getSelectedProfile().getName().equalsIgnoreCase(profile.getName())) {
                                player.sendMessage(Locale.PROFILE_CANT_DELETE.get());
                                return;
                            }
                            /* Confirm they want to delete the profile*/
                            new ProfileDeleteConfirmMenu(atlas, userProfile, profile).open(player);
                        }
                    }
            ));
        }
        buttons.put(getMenuType().getSize() - 1, new SimpleButton(
                new ItemstackBuilder(Material.valueOf(atlas.getConfig().getString("PROFILES-MENU.NEW-PROFILE-BUTTON.MATERIAL")))
                        .name(atlas.getConfig().getString("PROFILES-MENU.NEW-PROFILE-BUTTON.NAME"))
                        .lore(atlas.getConfig().getStringList("PROFILES-MENU.NEW-PROFILE-BUTTON.LORE")).build(),
                false,
                (InventoryClickEvent event) -> {
                    if (userProfile.getAllProfiles().size() >= userProfile.getMaxProfiles()) {
                        player.sendMessage(Locale.TOO_MANY_PROFILES.get());
                        return;
                    }
                    player.closeInventory();
                    player.sendMessage(Locale.NEW_PROFILE_PROCEDURE.get());
                    ProfileListener.getNewProfileList().add(player.getUniqueId());
                }
        ));
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return player.getName() + "'s Profiles";
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SINGLE_ROW_CHEST;
    }
}
