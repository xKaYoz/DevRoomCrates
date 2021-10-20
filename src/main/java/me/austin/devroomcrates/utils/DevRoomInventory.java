package me.austin.devroomcrates.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface DevRoomInventory extends InventoryHolder {

    void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type);

}
