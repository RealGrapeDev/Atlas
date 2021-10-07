package me.charlotte.atlas.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/4/2021 / 6:48 PM
 * Atlas / me.charlotte.atlas.utils
 */
public class Config extends YamlConfiguration {

    private final JavaPlugin plugin;
    private final String fileName;

    public Config(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File file = new File(plugin.getDataFolder(), this.fileName);
        if (!file.exists()) {
            if (plugin.getResource(this.fileName) != null) {
                plugin.saveResource(this.fileName, false);
            } else {
                save();
            }
            try {
                load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            save();
            return;
        }
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path) {
        if (super.getString(path) == null) {
            return "Invalid Path.";
        }
        return ChatColor.translateAlternateColorCodes('&', super.getString(path));
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    public void save() {
        try {
            this.save(new File(plugin.getDataFolder(), this.fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
