package me.austin.devroomcrates.guis;

import lombok.Getter;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.rewards.Reward;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CrateRewardsGUI implements DevRoomInventory {

    Chat chat = DevRoomCrates.getInstance().getChat();
    @Getter
    private final Crate crate;
    private int page;
    @Getter
    private static final HashMap<UUID, CrateRewardsGUI> addRewardMap = new HashMap<>();

    public CrateRewardsGUI(Crate crate, int page) {
        this.crate = crate;
        this.page = page;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                p.openInventory(new CrateEditSelectGUI(crate).getInventory());
                return;
            case 45:
                page--;
                p.openInventory(this.getInventory());
                return;
            case 49:
                chat.sendMessage(p, "&eRight click to cancel");
                chat.sendMessage(p, "&e&lLeft click with the item in your hand that you want to add as a reward.");
                addRewardMap.put(p.getUniqueId(), this);
                p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                return;
            case 53:
                page++;
                p.openInventory(this.getInventory());
                return;
        }

        //Is a reward
        if (slot >= 8 && slot <= 45) {
            Reward reward = DevRoomCrates.getInstance().getRewardManager().getFromDisplay(item);

            if(type.isLeftClick()) {
                p.openInventory(new RewardEditGUI(reward).getInventory());
            } else if(type.isShiftClick() && type.isRightClick()) {
                //TODO Remove Reward
            }

        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, chat.format(crate.getDisplayName() + " &bRewards"));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn to the Crate Edit Menu.")));

        int end = page * 36;
        int start = end - 36;

        List<Reward> rewards = crate.getRewards();

        int t;

        crate.calculateWeights();

        for (t = start; t < end; t++) {
            if (rewards.size() == t) {
                break;
            }
            Reward reward = rewards.get(t);
            reward.setupDisplay();
            ItemStack local = reward.getDisplay().clone();
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

        inv.setItem(49, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, "&a&lAdd Reward", Collections.singletonList("&7&oClick to add a reward to the " + crate.getDisplayName() + " &7&ocrate.")));
        //Next Page
        if (rewards.size() > end) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Next Page", Collections.singletonList("&7Go to the next page."));
            inv.setItem(53, next);
        }

        return inv;
    }
}
