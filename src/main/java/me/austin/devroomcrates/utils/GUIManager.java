package me.austin.devroomcrates.utils;

import me.austin.devroomcrates.DevRoomCrates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManager implements Listener {

    public ConcurrentHashMap<String, DevRoomInventory> getGui() {
        return gui;
    }
    public ConcurrentHashMap<String, DevRoomInventory> gui = new ConcurrentHashMap<>();

    Chat chat = DevRoomCrates.getInstance().getChat();

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if(e.getInventory().getHolder() instanceof DevRoomInventory) {
            getGui().put(e.getPlayer().getUniqueId().toString(), (DevRoomInventory) e.getInventory().getHolder());
            //chat.alert("PUT " + e.getPlayer().getUniqueId() + " with " + ((AkarianInventory) e.getInventory().getHolder()).getClass());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(e.getInventory().getHolder() instanceof DevRoomInventory) {
            getGui().remove(e.getPlayer().getUniqueId().toString(), (DevRoomInventory) e.getInventory().getHolder());
            //chat.alert("REMOVED " + e.getPlayer().getUniqueId() + " with " + ((AkarianInventory) e.getInventory().getHolder()).getClass());

        }
    }

    public void closeAllInventories() {
        for(String uuid : getGui().keySet()) {
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));
            if(p != null) {
                p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        int slot = e.getRawSlot();
        if((e.getInventory().getHolder() instanceof DevRoomInventory)) {

            e.setCancelled(true);

            if (item == null || !item.hasItemMeta()) return;

            getGui().get(p.getUniqueId().toString()).onGUIClick(e.getInventory(), p, slot, item, e.getClick());
        }
    }

}
