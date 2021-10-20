package me.austin.devroomcrates.crates;

import lombok.Getter;
import lombok.Setter;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.rewards.Reward;
import me.austin.devroomcrates.utils.RandomCollection;
import me.austin.devroomcrates.utils.UUIDDataType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Crate {

    @Getter @Setter
    private String displayName;
    @Getter
    private final UUID id;
    @Getter
    private final List<Reward> rewards;
    @Getter
    private ItemStack displayItem;
    @Getter
    private int totalWeights;
    @Getter
    private ItemStack key;
    @Getter
    private List<String> locations;

    public Crate(UUID id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        rewards = new ArrayList<>();
        locations = new ArrayList<>();
    }

    public void calculateWeights() {
        int total = 0;
        for(Reward reward : getRewards()) {
            total += reward.getWeight();
        }
        totalWeights = total;
        for(Reward reward : rewards) {
            reward.calculatePercentage();
        }
    }

    public void setupDisplay() {
        displayItem = new ItemStack(Material.CHEST);
        List<String> lore = new ArrayList<>();

        lore.add("&8&m&l-------------------");
        lore.add("");
        lore.add("  &fRewards &8 &e" + rewards.size());
        lore.add("");
        lore.add("&8&m&l-------------------");

        NamespacedKey key = new NamespacedKey(DevRoomCrates.getInstance(), "crate-id");
        ItemMeta meta = displayItem.getItemMeta();

        meta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        meta.setLore(DevRoomCrates.getInstance().getChat().formatList(lore));

        meta.setDisplayName(DevRoomCrates.getInstance().getChat().format(displayName));

        displayItem.setItemMeta(meta);
    }

    public void setKey(ItemStack itemStack) {
        key = itemStack;
        DevRoomCrates.getInstance().getCrateManager().saveKeyItem(this);
    }

    public Reward getRandomReward() {
        RandomCollection<Reward> rewards = new RandomCollection<>();

        for(Reward reward : getRewards()) {
            rewards.add(reward.getWeight(), reward);
        }

        return rewards.next();
    }

    public void deleteLocations() {

        ArrayList<String> remove = new ArrayList<>();

        for(String loc : locations) {
            DevRoomCrates.getInstance().getCrateManager().deleteLocation(DevRoomCrates.getInstance().getCrateManager().decode(loc));
            remove.add(loc);
        }

        locations.removeAll(remove);
    }

    public void deleteRewards() {

        for(Reward reward : rewards) {
            deleteReward(reward);
        }

    }

    public void deleteReward(Reward reward) {
        reward.delete();
        rewards.remove(reward);
    }

}
