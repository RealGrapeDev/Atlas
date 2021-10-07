package me.charlotte.atlas.command;

import me.charlotte.atlas.Atlas;
import me.charlotte.atlas.menu.ProfileMenu;
import me.charlotte.atlas.profile.UserProfile;
import me.charlotte.atlas.utils.Locale;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/5/2021 / 3:35 PM
 * Atlas / me.charlotte.atlas.command
 */
public class ProfileCommand implements CommandExecutor {

    private final Atlas atlas;

    public ProfileCommand(Atlas atlas) {
        this.atlas = atlas;
        atlas.getCommand("profile").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        /* Check if CommandSender is a player*/
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed in game.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("atlas.profile")) {
            sender.sendMessage(Locale.NO_PERMISSION.get());
            return true;
        }
        UserProfile userProfile = UserProfile.getByPlayer(player);
        /* Check if commands has no arguments. Then we open the ProfileMenu */
        if (args.length == 0) {
            new ProfileMenu(atlas, userProfile).open(player);
            return true;
        }
        helpMessage(player);
        return true;
    }

    private void helpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /profile");
    }
}
