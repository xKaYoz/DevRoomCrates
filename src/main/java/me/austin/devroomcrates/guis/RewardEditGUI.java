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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class RewardEditGUI implements DevRoomInventory {

    @Getter
    private final Reward reward;
    @Getter
    public static final HashMap<UUID, RewardEditGUI> listAmount = new HashMap<>();
    @Getter
    public static final HashMap<UUID, RewardEditGUI> listWeight = new HashMap<>();
    Chat chat = DevRoomCrates.getInstance().getChat();

    public RewardEditGUI(Reward reward) {
        this.reward = reward;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                p.openInventory(new CrateRewardsGUI(DevRoomCrates.getInstance().getCrateManager().getCrate(reward.getCrateID()), 1).getInventory());
                listAmount.remove(p.getUniqueId());
                listWeight.remove(p.getUniqueId());
                break;
            case 10:
                listAmount.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the amount to set to:");
                break;
            case 13:
                listWeight.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the weight to set to:");
                break;
            case 16:
                Crate crate = DevRoomCrates.getInstance().getCrateManager().getCrate(reward.getCrateID());
                chat.sendMessage(p, "&fYou have removed the " + chat.formatItem(reward.getItemStack()) + " &freward from the " + crate.getDisplayName()
                 + " &fcrate.");
                crate.deleteReward(reward);
                crate.calculateWeights();
                p.openInventory(new CrateRewardsGUI(crate, 1).getInventory());
                break;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format("&bRewards &7> &r" + chat.formatItem(reward.getItemStack())));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the Rewards Menu.")));

        inv.setItem(10, ItemBuilder.build(Material.ARROW, 1, "&eChange Amount", Arrays.asList("  &fAmount &8&m&l-&e " + reward.getItemStack().getAmount(), "&7&oClick to change the amount of items to give.")));

        inv.setItem(13, ItemBuilder.build(Material.ANVIL, 1, "&eChange Weight", Arrays.asList("  &fWeight &8&m&l-&e " + reward.getWeight(), "&7&oClick to change the Percentage weight.")));

        inv.setItem(16, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&c&lDelete", Collections.singletonList("&cClick to delete.")));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        reward.setupDisplay();
        inv.setItem(22, reward.getDisplay());

        return inv;
    }
}
