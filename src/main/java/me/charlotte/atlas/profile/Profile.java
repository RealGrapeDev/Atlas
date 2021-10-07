package me.charlotte.atlas.profile;

import me.charlotte.atlas.Atlas;
import me.charlotte.atlas.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/4/2021 / 7:03 PM
 * Atlas / me.charlotte.atlas.profile
 */
public class Profile {

    private String name;

    /* All of the players attributes */
    private float experienceProgress;
    private int experienceLevel;
    private float exhaustion;
    private int foodlevel;
    private double healthLevel;

    private double healthScale;
    private float saturation;
    private GameMode gameMode;

    /* Players Location and bed spawn location */
    private Location location;
    private Location bedLocation;

    /* Players armor contents, inventory contents, and enderchest contents */
    private List<ItemStack> armorContents;
    private List<ItemStack> inventoryContents;
    private List<ItemStack> enderChestContents;

    /* Players armor contents, inventory contents, and enderchest contents */
    private List<PotionEffect> potionEffects;

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public List<ItemStack> getEnderChestContents() {
        return enderChestContents;
    }

    public void setEnderChestContents(List<ItemStack> enderChestContents) {
        this.enderChestContents = enderChestContents;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<ItemStack> getArmorContents() {
        return armorContents;
    }

    public void setArmorContents(List<ItemStack> armorContents) {
        this.armorContents = armorContents;
    }

    public List<ItemStack> getInventoryContents() {
        return inventoryContents;
    }

    public void setInventoryContents(List<ItemStack> inventoryContents) {
        this.inventoryContents = inventoryContents;
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public float getExperienceProgress() {
        return experienceProgress;
    }

    public void setExperienceProgress(float experienceProgress) {
        this.experienceProgress = experienceProgress;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(int experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }

    public int getFoodlevel() {
        return foodlevel;
    }

    public void setFoodlevel(int footlevel) {
        this.foodlevel = footlevel;
    }

    public double getHealthScale() {
        return healthScale;
    }

    public void setHealthScale(double healthScale) {
        this.healthScale = healthScale;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public double getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(double healthLevel) {
        this.healthLevel = healthLevel;
    }

    public void setAttributes(Player player) {
        this.armorContents = Arrays.asList(player.getInventory().getArmorContents());
        this.enderChestContents = Arrays.asList(player.getEnderChest().getContents());
        this.inventoryContents = Arrays.asList(player.getInventory().getContents());

        this.location = player.getLocation();

        this.potionEffects = new ArrayList<>(player.getActivePotionEffects());

        this.experienceProgress = player.getExp();
        this.experienceLevel = player.getLevel();
        this.exhaustion = player.getExhaustion();
        this.foodlevel = player.getFoodLevel();
        this.healthLevel = player.getHealth();
        this.healthScale = player.getHealthScale();
        this.saturation = player.getSaturation();
        this.gameMode = player.getGameMode();
        this.bedLocation = player.getBedSpawnLocation();
    }

    public static CompletableFuture<Profile> createEmptyProfile(String name) {
        Profile profile = new Profile();


        profile.setName(name);
        profile.setLocation(Bukkit.getWorld("world").getSpawnLocation());
        profile.setArmorContents(new ArrayList<>());
        profile.setInventoryContents(new ArrayList<>());
        profile.setPotionEffects(new ArrayList<>());
        profile.setEnderChestContents(new ArrayList<>());
        profile.setBedLocation(null);
        profile.setExperienceLevel(0);
        profile.setExperienceProgress(0.0F);
        profile.setExhaustion(0);
        profile.setFoodlevel(20);
        profile.setHealthLevel(20);
        profile.setHealthScale(20);
        profile.setSaturation(0);
        profile.setGameMode(GameMode.SURVIVAL);

        return CompletableFuture.completedFuture(profile);
    }

    public static Profile createProfileFromPlayer(Player player, String string) {
        Profile profile = new Profile();
        profile.setName(string);
        profile.setLocation(player.getLocation());
        profile.setArmorContents(Arrays.asList(player.getInventory().getArmorContents()));
        profile.setInventoryContents(Arrays.asList(player.getInventory().getContents()));
        profile.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
        profile.setEnderChestContents(Arrays.asList(player.getEnderChest().getContents()));
        profile.setBedLocation(null);
        profile.setExperienceLevel(player.getLevel());
        profile.setExperienceProgress(player.getExp());
        profile.setExhaustion(player.getExhaustion());
        profile.setFoodlevel(player.getFoodLevel());
        profile.setHealthLevel(player.getHealth());
        profile.setHealthScale(player.getHealthScale());
        profile.setSaturation(player.getSaturation());
        profile.setGameMode(player.getGameMode());
        return profile;
    }

    public static Profile getByName(UserProfile userProfile, String name) {
        return userProfile.getAllProfiles().stream().filter(profile -> profile.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
