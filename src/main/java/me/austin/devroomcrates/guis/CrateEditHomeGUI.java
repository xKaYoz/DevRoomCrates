package me.austin.devroomcrates.guis;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomInventory;
import me.austin.devroomcrates.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CrateEditHomeGUI implements DevRoomInventory {

    Chat chat = DevRoomCrates.getInstance().getChat();
    int page;

    public CrateEditHomeGUI(int page) {
        this.page = page;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                return;
            case 45:
                page--;
                p.openInventory(this.getInventory());
                return;
            case 53:
                page++;
                p.openInventory(this.getInventory());
                return;
        }

        //Is a crate
        if (slot >= 8 && slot <= 45) {
            Crate crate = DevRoomCrates.getInstance().getCrateManager().getFromDisplay(item);

            if(type.isLeftClick()) {
                p.openInventory(new CrateEditSelectGUI(crate).getInventory());
            } else if(type.isShiftClick() && type.isRightClick()) {
                //TODO Remove crate
            }

        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, chat.format("&bDevRoomCrate Editor"));


        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lClose", Collections.singletonList("&7&oClose the menu.")));

        int end = page * 36;
        int start = end - 36;

        List<Crate> crates = DevRoomCrates.getInstance().getCrateManager().getCrates();

        int t;

        for (t = start; t < end; t++) {
            if (crates.size() == t) {
                break;
            }
            Crate crate = crates.get(t);
            crate.setupDisplay();
            ItemStack local = crate.getDisplayItem().clone();
            if (local.getAmount() > 64) {
                local.setAmount(64);
            }

            ItemMeta meta = local.getItemMeta();
            List<String> lore = meta.getLore();

            lore.add("&7&oLeft click to edit");
            lore.add("&c&oShift + Right Click to remove");

            meta.setLore(chat.formatList(lore));
            local.setItemMeta(meta);

            inv.addItem(local);
        }

        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Previous Page", Collections.singletonList("&7Go to the previous page."));
            inv.setItem(45, previous);
        }
        //Next Page
        if (crates.size() > end) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Next Page", Collections.singletonList("&7Go to the next page."));
            inv.setItem(53, next);
        }

        return inv;
    }
}
