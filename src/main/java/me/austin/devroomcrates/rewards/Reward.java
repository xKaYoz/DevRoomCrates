package me.austin.devroomcrates.rewards;

import lombok.Getter;
import lombok.Setter;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.utils.UUIDDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reward {

    @Getter
    private ItemStack itemStack;
    @Getter
    private int weight;
    @Getter @Setter
    private float percentage;
    @Getter
    private final UUID crateID;
    @Getter
    private final UUID id;
    @Getter
    private ItemStack display;

    public Reward(UUID id, UUID crateID, ItemStack itemStack, int weight) {
        this.itemStack = itemStack;
        this.id = id;
        this.crateID = crateID;
        this.weight = weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        DevRoomCrates.getInstance().getRewardManager().saveWeight(this);
        DevRoomCrates.getInstance().getCrateManager().getCrate(crateID).calculateWeights();
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        DevRoomCrates.getInstance().getRewardManager().saveItemStack(this);
    }

    public void setupDisplay() {

        display = itemStack.clone();

        List<String> lore = itemStack.hasItemMeta()? itemStack.getItemMeta().getLore() : new ArrayList<>();

        assert lore != null;

        lore.add("&8&m&l-------------------");
        lore.add("");
        if(itemStack.getAmount() > 64)
            lore.add("  &fAmount &8&m&l-&e " + itemStack.getAmount());
        lore.add("  &fChance &8≈ &e" + percentage + "%");
        lore.add("");
        lore.add("&8&m&l-------------------");

        NamespacedKey key = new NamespacedKey(DevRoomCrates.getInstance(), "reward-id");
        ItemMeta meta = display.getItemMeta();

        meta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        meta.setLore(DevRoomCrates.getInstance().getChat().formatList(lore));

        if(display.getAmount() > display.getMaxStackSize()) {
            display.setAmount(display.getMaxStackSize());
        }

        display.setItemMeta(meta);
    }

    public void calculatePercentage() {
        float p = (weight * 1F) / DevRoomCrates.getInstance().getCrateManager().getCrate(crateID).getTotalWeights();
        percentage = Float.parseFloat(String.format("%.2f", p * 100));
    }

    public void delete() {
        DevRoomCrates.getInstance().getRewardManager().deleteReward(this);
    }

}
