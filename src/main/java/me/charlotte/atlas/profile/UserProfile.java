package me.charlotte.atlas.profile;

import me.charlotte.atlas.Atlas;
import me.charlotte.atlas.utils.ItemUtil;
import me.charlotte.atlas.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.*;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/4/2021 / 7:14 PM
 * Atlas / me.charlotte.atlas.profile
 */
public class UserProfile {

    private Atlas atlas = Atlas.getPlugin(Atlas.class);
    private static HashMap<UUID, UserProfile> profiles = new HashMap<>();

    private UUID uuid;

    private int maxProfiles = atlas.getConfig().getInt("MAX-PROFILES-PER-USER");

    private List<Profile> allProfiles;
    private Profile selectedProfile;

    public UserProfile(UUID uuid) {
        this.uuid = uuid;
    }

    /* Load UserProfile from data.yml. If user data is null, we create a new UserProfile for them */
    public void load() {
        ConfigurationSection configurationSection = atlas.getData().getConfigurationSection(uuid.toString());
        this.allProfiles = new ArrayList<>();

        if (configurationSection != null) {
            for (String sectionKey : configurationSection.getConfigurationSection("PROFILES").getKeys(false)) {
                ConfigurationSection section = atlas.getData().getConfigurationSection(configurationSection.getName() + ".PROFILES." + sectionKey);
                Profile profile = new Profile();

                profile.setName(section.getName());

                try {
                    profile.setArmorContents(ItemUtil.itemStackArrayFromBase64(section.getString("INVENTORY.ARMOR")));
                    profile.setInventoryContents(ItemUtil.itemStackArrayFromBase64(section.getString("INVENTORY.CONTENTS")));
                    profile.setEnderChestContents(ItemUtil.itemStackArrayFromBase64(section.getString("INVENTORY.ENDERCHEST")));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                ConfigurationSection attributeSection = atlas.getData().getConfigurationSection(configurationSection.getName() + ".PROFILES." + sectionKey + ".ATTRIBUTES");

                profile.setExperienceLevel(attributeSection.getInt("EXPERIENCE-LEVEL"));
                profile.setExperienceProgress((float) attributeSection.getDouble("EXPERIENCE-PROGRESS"));
                profile.setExhaustion((float) attributeSection.getDouble("EXHAUSTION"));
                profile.setFoodlevel(attributeSection.getInt("FOODLEVEL"));
                profile.setHealthLevel(attributeSection.getDouble("HEALTH"));
                profile.setHealthScale(attributeSection.getDouble("HEALTHSCALE"));
                profile.setSaturation((float) attributeSection.getDouble("SATURATION"));
                profile.setGameMode(GameMode.valueOf(attributeSection.getString("GAMEMODE")));

                profile.setLocation(LocationUtil.deserialize(section.getString("LOCATION")));
                profile.setBedLocation(section.getString("BEDLOCATION").equalsIgnoreCase("null")
                        ? null
                        : LocationUtil.deserialize(section.getString("BEDLOCATION")));

                List<PotionEffect> potionEffects = new ArrayList<>();
                ConfigurationSection potionsSection = section.getConfigurationSection("POTION_EFFECTS");

                if (potionsSection != null) {
                    for (String potion_effects : potionsSection.getKeys(false)) {
                        ConfigurationSection potionsection = section.getConfigurationSection("POTION_EFFECTS");
                        PotionEffectType byName = PotionEffectType.getByName(potion_effects);

                        if (byName == null) {
                            continue;
                        }

                        PotionEffect potionEffect = new PotionEffect(byName, potionsection.getInt("TIME"), potionsection.getInt("LEVEL"));

                        potionEffects.add(potionEffect);
                    }
                }
                profile.setPotionEffects(potionEffects);
                this.allProfiles.add(profile);
            }
            this.selectedProfile = Profile.getByName(this, atlas.getData().getString(this.uuid.toString() + ".SELECTED-PROFILE"));
            profiles.put(uuid, this);
            return;
        }
        Profile profile = Profile.createProfileFromPlayer(Bukkit.getPlayer(uuid), "Default");
        this.selectedProfile = profile;
        this.allProfiles.add(profile);
        profiles.put(uuid, this);
        this.save();
    }

    /* Save all of the UserProfile's profiles.*/
    public void save() {
        atlas.getData().set(uuid.toString(), "");
        atlas.getData().set(uuid.toString() + ".SELECTED-PROFILE", selectedProfile.getName());
        atlas.getData().set(uuid.toString() + ".PROFILES", "");

        for (Profile profile : allProfiles) {
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".NAME", profile.getName());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".LOCATION", LocationUtil.serialize(profile.getLocation()));
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".BEDLOCATION", profile.getBedLocation() == null ?
                    "null" : LocationUtil.serialize(profile.getLocation()));

            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.EXPERIENCE-PROGRESS", profile.getExperienceProgress());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.EXPERIENCE-LEVEL", profile.getExperienceLevel());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.EXHAUSTION", profile.getExhaustion());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.FOODLEVEL", profile.getFoodlevel());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.HEALTH", profile.getHealthLevel());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.HEALTHSCALE", profile.getHealthScale());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.SATURATION", profile.getSaturation());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.GAMEMODE", profile.getGameMode().toString());

            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".INVENTORY.ARMOR", ItemUtil.itemStackArrayToBase64(profile.getArmorContents()));
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".INVENTORY.CONTENTS", ItemUtil.itemStackArrayToBase64(profile.getInventoryContents()));
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".INVENTORY.ENDERCHEST", ItemUtil.itemStackArrayToBase64(profile.getEnderChestContents()));
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".POTION_EFFECTS", "");

            for (PotionEffect potionEffect : profile.getPotionEffects()) {
                atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".POTION_EFFECTS." + potionEffect.getType() + ".TIME", potionEffect.getDuration());
                atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".POTION_EFFECTS." + potionEffect.getType() + ".LEVEL", potionEffect.getAmplifier());
            }
        }
        atlas.getData().save();
    }

    /* Get a UserProfile by the player. Otherwise load it from the config. */
    public static UserProfile getByPlayer(Player player) {
        if (profiles.containsKey(player.getUniqueId())) {
            return profiles.get(player.getUniqueId());
        }
        return new UserProfile(player.getUniqueId());
    }

    /* Save a single profile */
    public void saveProfile(Profile profile, Player player) {
        atlas.getData().set(uuid.toString(), "");
        atlas.getData().set(uuid.toString() + ".SELECTED-PROFILE", selectedProfile.getName());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".NAME", profile.getName());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".LOCATION", LocationUtil.serialize(player.getLocation()));
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".BEDLOCATION", profile.getBedLocation() == null ?
                "null" : LocationUtil.serialize(profile.getLocation()));

        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.EXPERIENCE-PROGRESS", profile.getExperienceProgress());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.EXPERIENCE-LEVEL", profile.getExperienceLevel());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.EXHAUSTION", profile.getExhaustion());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.FOODLEVEL", profile.getFoodlevel());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.HEALTH", profile.getHealthLevel());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.HEALTHSCALE", profile.getHealthScale());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.SATURATION", profile.getSaturation());
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".ATTRIBUTES.GAMEMODE", profile.getGameMode().toString());

        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".INVENTORY.ARMOR", ItemUtil.itemStackArrayToBase64(Arrays.asList(player.getInventory().getArmorContents())));
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".INVENTORY.CONTENTS", ItemUtil.itemStackArrayToBase64(Arrays.asList(player.getInventory().getContents())));
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".INVENTORY.ENDERCHEST", ItemUtil.itemStackArrayToBase64(Arrays.asList(player.getEnderChest().getContents())));
        atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".POTION_EFFECTS", "");

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".POTION_EFFECTS." + potionEffect.getType() + ".TIME", potionEffect.getDuration());
            atlas.getData().set(uuid.toString() + ".PROFILES." + profile.getName() + ".POTION_EFFECTS." + potionEffect.getType() + ".LEVEL", potionEffect.getAmplifier());
        }
    }

    public void setActiveProfile(Profile profile, Player player) {
        Profile selectedProfile = getSelectedProfile();
        selectedProfile.setAttributes(player);
        saveProfile(selectedProfile, player);
        setSelectedProfile(profile);
    }

    /* Setup a player's attributes based on their selected profile */
    public void setupProfile(Player player) {
        Profile selectedProfile = this.selectedProfile;
        player.teleport(selectedProfile.getLocation());
        player.getInventory().setArmorContents(selectedProfile.getArmorContents().toArray(new ItemStack[0]));
        player.getInventory().setContents(selectedProfile.getInventoryContents().toArray(new ItemStack[0]));
        player.getEnderChest().setContents(selectedProfile.getEnderChestContents().toArray(new ItemStack[0]));

        player.setExp(selectedProfile.getExperienceProgress());
        player.setLevel(selectedProfile.getExperienceLevel());
        player.setExhaustion(selectedProfile.getExhaustion());
        player.setFoodLevel(selectedProfile.getFoodlevel());
        player.setHealthScale(selectedProfile.getHealthScale());
        player.setHealth(selectedProfile.getHealthLevel());
        player.setSaturation(selectedProfile.getSaturation());
        player.setGameMode(selectedProfile.getGameMode());
        player.setBedSpawnLocation(selectedProfile.getBedLocation());

        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }

        for (PotionEffect potionEffect : selectedProfile.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }
    }

    public static HashMap<UUID, UserProfile> getProfiles() {
        return profiles;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<Profile> getAllProfiles() {
        return this.allProfiles;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(Profile profile) {
        this.selectedProfile = profile;
    }

    public int getMaxProfiles() {
        return maxProfiles;
    }

}
