package me.charlotte.atlas;

import com.ericstolly.menu.MenuListener;
import me.charlotte.atlas.command.ProfileCommand;
import me.charlotte.atlas.listeners.ProfileListener;
import me.charlotte.atlas.profile.Profile;
import me.charlotte.atlas.profile.UserProfile;
import me.charlotte.atlas.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/4/2021 / 6:38 PM
 * Atlas / me.charlotte.atlas
 */
public class Atlas extends JavaPlugin {

    private Config lang, config, data;

    private ExecutorService executorService;

    @Override
    public void onEnable() {
        /* Create a new executor service with 4 threads */
        executorService = Executors.newFixedThreadPool(4);

        /* Loading configuration */
        lang = new Config(this, "lang.yml");
        config = new Config(this, "config.yml");
        data = new Config(this, "data.yml");

        /* Registering Command */
        new ProfileCommand(this);

        /* Registering Listeners */
        this.getServer().getPluginManager().registerEvents(new ProfileListener(this), this);
        /* We had to override the MenuAPI's Menu Listener to support 1.17*/
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);

    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            UserProfile userProfile = UserProfile.getByPlayer(onlinePlayer);
            userProfile.getSelectedProfile().setAttributes(onlinePlayer);
            userProfile.save();
        }
    }

    public Config getLang() {
        return lang;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    public Config getData() {
        return data;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

}
