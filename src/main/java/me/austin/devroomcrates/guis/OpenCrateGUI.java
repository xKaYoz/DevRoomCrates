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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class OpenCrateGUI implements DevRoomInventory {

    private final Crate crate;
    private final Player player;
    Chat chat = DevRoomCrates.getInstance().getChat();

    public OpenCrateGUI(Player player, Crate crate) {
        this.crate = crate;
        this.player = player;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format(crate.getDisplayName()));

        //Top Lining
        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(4, ItemBuilder.build(Material.REDSTONE_TORCH, 1, "&6Reward", Collections.singletonList("&7&oYour reward is below.")));

        //Bottom Lining
        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(22, ItemBuilder.build(Material.REDSTONE_TORCH, 1, "&6Reward", Collections.singletonList("&7&oYour reward is above.")));

        return inv;
    }
}
