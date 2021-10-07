package com.ericstolly.menu;

import com.ericstolly.menu.button.MenuButton;
import com.ericstolly.menu.button.update.ButtonUpdateType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/5/2021 / 11:13 PM
 * Atlas / com.ericstolly.menu
 */
public class MenuListener implements Listener {

    /*
     We had to override the MenuAPI's MenuListener to use 1.17.
     MenuAPI -> https://github.com/ericstolly/menu-api
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        // Make sure it was a player that clicked in said inventory.
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Menu menu = Menu.getByPlayer(player);

            // Make sure player has an open menu.
            if (menu == null) return;

            // Make sure the inventory said player clicked in, actually exists.
            if (event.getClickedInventory() == null) return;

            // Make sure the player isn't just clicking on his own inventory.
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;

            // Make sure the inventory is titled the same as the menu.
            if (!event.getView().getTitle().equals(menu.getTitle(player))) return;

            // Make sure a button exists at the clicked inventory slot.
            if (!menu.getButtons(player).containsKey(event.getSlot())) return;

            MenuButton button = menu.getButtons(player).get(event.getSlot());

            // Make sure the button said player clicked on, actually exists.
            if (button == null) return;

            // Prevent editing non-editable buttons, any button inside an autoupdating menu and buttons with a listener..
            if (!button.isEditable(player) || !menu.getUpdateType().equals(ButtonUpdateType.NONE) || (button.getButtonListener(player) != null)) {
                event.setCancelled(true);
            }

            // Prevent shift clicking items in or out of any menu that isn't a chest to prevent StackOverflow.
            if (!menu.getMenuType().getInventoryType().equals(InventoryType.CHEST)) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                }
            }

            // Update menu on clicked if set to do so.
            if (menu.getUpdateType().equals(ButtonUpdateType.ON_CLICK)) {
                menu.open(player, true);
            }

            // Fire defined MenuButtonListener# on button click.
            if (button.getButtonListener(player) != null) {
                button.getButtonListener(player).onButtonClick(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            Menu menu = Menu.getByPlayer(player);

            // Make sure player has an open menu.
            if (menu != null) {
                // Cancel the ButtonUpdateTask when menu is closed.
                if (menu.getUpdateRunnable() != null) {
                    menu.getUpdateRunnable().cancel();
                }

                // Remove the menu from the list of opened menus.
                Menu.getOpenedMenus().remove(player);
            }
        }
    }

}
