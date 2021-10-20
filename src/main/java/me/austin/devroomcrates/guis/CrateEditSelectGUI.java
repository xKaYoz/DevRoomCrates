package me.austin.devroomcrates.guis;

import lombok.Getter;
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
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class CrateEditSelectGUI implements DevRoomInventory {

    Chat chat = DevRoomCrates.getInstance().getChat();
    @Getter
    private final Crate crate;
    @Getter
    private static final HashMap<UUID, CrateEditSelectGUI> displayMap = new HashMap<>();

    public CrateEditSelectGUI(Crate crate) {
        this.crate = crate;
    }


    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                p.openInventory(new CrateEditHomeGUI(1).getInventory());
                break;
            case 10:
                p.openInventory(new CrateRewardsGUI(crate, 1).getInventory());
                break;
            case 12:
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the new price of your auction...");
                displayMap.put(p.getUniqueId(), this);
                p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                break;
            case 14:
                //TODO Crate Key Editor
                break;
            case 16:
                chat.sendMessage(p, "&fYou have deleted " + crate.getDisplayName());
                DevRoomCrates.getInstance().getCrateManager().deleteCrate(crate);
                p.openInventory(new CrateEditHomeGUI(1).getInventory());
                break;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format("&b" + crate.getDisplayName() + "&b Editor"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the Crates Menu.")));

        inv.setItem(10, ItemBuilder.build(Material.BOOK, 1, "&bRewards", Collections.singletonList("&7&oClick to edit the rewards.")));

        inv.setItem(12, ItemBuilder.build(Material.PAPER, 1, "&bDisplay Name", Collections.singletonList("&7&oClick to edit the display name.")));

        inv.setItem(14, ItemBuilder.build(crate.getKey().getType(), 1, crate.getKey().getItemMeta().getDisplayName(), Collections.singletonList("&7&oClick to edit the crate key.")));

        inv.setItem(16, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&c&lDelete", Collections.singletonList("&cClick to delete.")));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        crate.setupDisplay();

        inv.setItem(22, crate.getDisplayItem());

        return inv;
    }
}
