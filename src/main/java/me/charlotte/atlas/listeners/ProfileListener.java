package me.charlotte.atlas.listeners;

import me.charlotte.atlas.Atlas;
import me.charlotte.atlas.menu.ProfileLoadMenu;
import me.charlotte.atlas.profile.Profile;
import me.charlotte.atlas.profile.UserProfile;
import me.charlotte.atlas.utils.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/4/2021 / 6:45 PM
 * Atlas / me.charlotte.atlas.listeners
 */
public class ProfileListener implements Listener {

    private final Atlas atlas;

    private static final HashMap<UUID, Profile> renameProfileMap = new HashMap<>();
    private static final List<UUID> newProfileList = new ArrayList<>();


    public ProfileListener(Atlas atlas) {
        this.atlas = atlas;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserProfile profile = UserProfile.getByPlayer(player);

        /* We load the profile from the data.yml and setup their profile */
        profile.load();
        profile.setupProfile(player);
        player.sendMessage(Locale.PROFILE_LOADED.get(profile.getSelectedProfile().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UserProfile profile = UserProfile.getByPlayer(player);
        Profile selectedProfile = profile.getSelectedProfile();

        /* Set a profiles attributes from player */
        selectedProfile.setAttributes(player);

        renameProfileMap.remove(player.getUniqueId());
        newProfileList.remove(player.getUniqueId());
        /* We save the profile on an executor service to prevent lag */
        atlas.getExecutorService().execute(profile::save);
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UserProfile userProfile = UserProfile.getByPlayer(player);

        /* Check if the player is in the rename profile procedure */
        if (!renameProfileMap.containsKey(player.getUniqueId())) {
            return;
        }
        /* Cancel the event so the chat message will not appear in chat */
        event.setCancelled(true);

        Profile profile = renameProfileMap.get(player.getUniqueId());

        player.sendMessage(Locale.PROFILE_RENAMED.get(profile.getName(), event.getMessage()));

        profile.setName(event.getMessage());

        /* We save the profile on an executor service to prevent lag */
        atlas.getExecutorService().execute(userProfile::save);

        /* Remove them from the rename profile procedure */
        renameProfileMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UserProfile userProfile = UserProfile.getByPlayer(player);

        /* Check if the player is in the new profile procedure */
        if (!newProfileList.contains(player.getUniqueId())) {
            return;
        }
        /* Cancel the event so the chat message will not appear in chat */
        event.setCancelled(true);

        Profile profile = Profile.getByName(userProfile, event.getMessage());

        /* Check if the profile already exists with the name */
        if (profile != null) {
            player.sendMessage(Locale.PROFILE_EXISTS.get());
            return;
        }
        Profile.createEmptyProfile(event.getMessage()).whenComplete((profile1, throwable) -> {
            atlas.getExecutorService().execute(() -> {
                userProfile.getAllProfiles().add(profile1);
                userProfile.save();
            });
            /* Open the menu to confirm if they want to load the profile
             * This has to be done on the main thread since InventoryOpenEvent cannot be ran asynchronously */
            new BukkitRunnable() {
                @Override
                public void run() {
                    new ProfileLoadMenu(atlas, userProfile, profile1).open(player);
                }
            }.runTask(atlas);
        });
    }


    public static HashMap<UUID, Profile> getRenameProfileMap() {
        return renameProfileMap;
    }

    public static List<UUID> getNewProfileList() {
        return newProfileList;
    }

}
