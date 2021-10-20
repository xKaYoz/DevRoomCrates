package me.austin.devroomcrates.rewards;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.users.User;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.MySQL;
import me.austin.devroomcrates.utils.UUIDDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class RewardManager {

    private final List<Reward> rewards;
    private final Chat chat;
    private final MySQL mySQL;

    public RewardManager() {
        rewards = new ArrayList<>();
        chat = DevRoomCrates.getInstance().getChat();
        mySQL = DevRoomCrates.getInstance().getMySQL();
    }

    public Reward getReward(UUID id) {
        for(Reward reward : rewards) {
            if(reward.getId().toString().equals(id.toString())) return reward;
        }
        return null;
    }

    public void deleteReward(Reward reward) {
        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getRewardsTable() + " WHERE ID=?");

            statement.setString(1, reward.getId().toString());

            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Reward createReward(UUID id, Crate crate, ItemStack itemStack, int weight) {

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getRewardsTable() + " (ID,CRATE_ID,ITEM_STACK,WEIGHT) VALUES (?,?,?,?)");

            statement.setString(1, id.toString());
            statement.setString(2, crate.getId().toString());
            statement.setString(3, encode(itemStack, false));
            statement.setInt(4, weight);

            Reward reward = new Reward(id, crate.getId(), itemStack, weight);

            rewards.add(reward);

            crate.calculateWeights();

            statement.executeUpdate();
            statement.close();

            crate.getRewards().add(reward);

            return reward;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadRewards() {

        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getRewardsTable());

            ResultSet rs = statement.executeQuery();

            int loaded = 0;

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString(1));
                UUID crateID = UUID.fromString(rs.getString(2));
                ItemStack itemStack = decode(rs.getString(3));
                int weight = rs.getInt(4);

                Reward reward = new Reward(id, crateID, itemStack, weight);
                rewards.add(reward);
                loaded++;
            }

            statement.close();
            chat.log("Loaded " + loaded + " rewards.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadRewards(Crate crate) {

        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getRewardsTable() + " WHERE CRATE_ID=?");

            statement.setString(1, crate.getId().toString());

            ResultSet rs = statement.executeQuery();

            int loaded = 0;

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString(1));
                ItemStack itemStack = decode(rs.getString(3));
                int weight = rs.getInt(4);

                Reward reward = new Reward(id, crate.getId(), itemStack, weight);
                rewards.add(reward);
                crate.getRewards().add(reward);
                loaded++;
            }

            statement.close();
            chat.log("Loaded " + loaded + " rewards for crate " + crate.getDisplayName() + "&r.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveItemStack(Reward reward) {

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getRewardsTable() + " SET ITEM_STACK=? WHERE ID=?");

            statement.setString(1, encode(reward.getItemStack(), false));
            statement.setString(2, reward.getId().toString());

            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveWeight(Reward reward) {

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getRewardsTable() + " SET WEIGHT=? WHERE ID=?");

            statement.setInt(1, reward.getWeight());
            statement.setString(2, reward.getId().toString());

            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Encode an ItemStack to a String with Base64
     *
     * @param itemStack - ItemStack to encode
     * @param asOne - If to encode as one.
     * @return - String of encoded ItemStack
     */
    public String encode(ItemStack itemStack, boolean asOne) {
        ItemStack local = itemStack;
        if(asOne) {
            local = local.asOne();
        }
        return new String(Base64.getEncoder().encode(local.serializeAsBytes()));
    }

    /**
     * Decode a String to an ItemStack with Base64
     *
     * @param string - String of ItemStack encoded with Base64
     * @return - Decoded ItemStack
     */
    public ItemStack decode(String string) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(string.getBytes()));
    }

    public Reward getFromDisplay(ItemStack displayItem) {
        if (!displayItem.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(DevRoomCrates.getInstance(), "reward-id");
        ItemMeta itemMeta = displayItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, new UUIDDataType())) {
            return getReward(container.get(key, new UUIDDataType()));
        }
        return null;
    }

}
