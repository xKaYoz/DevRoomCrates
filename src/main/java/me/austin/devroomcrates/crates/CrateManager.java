package me.austin.devroomcrates.crates;

import lombok.Getter;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.ItemBuilder;
import me.austin.devroomcrates.utils.MySQL;
import me.austin.devroomcrates.utils.UUIDDataType;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class CrateManager {

    @Getter
    private final List<Crate> crates;
    private final Chat chat;
    private final MySQL mySQL;

    public CrateManager() {
        crates = new ArrayList<>();
        chat = DevRoomCrates.getInstance().getChat();
        mySQL = DevRoomCrates.getInstance().getMySQL();
    }

    public boolean isKey(ItemStack itemStack, Crate crate) {

        if (!itemStack.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey(DevRoomCrates.getInstance(), "crate-id");
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, new UUIDDataType())) {
            if(crate.getId().toString().equals(container.get(key, new UUIDDataType()).toString())) return true;
        }
        return false;

    }

    public Crate createCrate(UUID id, String displayName) {

        try {

            Crate crate = new Crate(id, displayName);
            crate.setKey(defaultKey(crate));

            crates.add(crate);

            PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getCratesTable() + " (ID,DISPLAY,KEY_ITEM) VALUES (?,?,?)");

            statement.setString(1, id.toString());
            statement.setString(2, displayName);
            statement.setString(3, DevRoomCrates.getInstance().getRewardManager().encode(crate.getKey(), true));

            statement.executeUpdate();
            statement.close();

            return crate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createLocation(Location location, Crate crate) {

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getLocationsTable() + " (LOCATION,CRATE_ID) VALUES (?,?)");

            statement.setString(1, encode(location));
            statement.setString(2, crate.getId().toString());

            statement.executeUpdate();
            statement.close();

            crate.getLocations().add(encode(location));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteLocation(Location location) {
        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getLocationsTable() + " WHERE LOCATION=?");

            statement.setString(1, encode(location));

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCrates() {

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getCratesTable());

            ResultSet rs = statement.executeQuery();

            int loaded = 0;

            while (rs.next()) {

                UUID id = UUID.fromString(rs.getString(1));
                String display = rs.getString(2);

                Crate crate = new Crate(id, display);

                crate.setKey(DevRoomCrates.getInstance().getRewardManager().decode(rs.getString(3)));

                DevRoomCrates.getInstance().getRewardManager().loadRewards(crate);

                crates.add(crate);

                crate.calculateWeights();

                loaded++;
            }

            chat.log("Loaded " + loaded + " crates");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getLocationsTable());

            ResultSet rs = statement.executeQuery();

            int loaded = 0;

            while (rs.next()) {
                Location loc = decode(rs.getString(1));
                Crate crate = getCrate(UUID.fromString(rs.getString(2)));

                crate.getLocations().add(encode(loc));
                loaded++;
            }

            chat.log("Loaded " + loaded + " locations.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Crate getCrate(UUID id) {
        for(Crate crate : crates) {
            if(crate.getId().toString().equals(id.toString())) return crate;
        }
        return null;
    }

    public Crate getCrateAtLocation(Location location) {
        for(Crate crate : crates) {
            if(crate.getLocations().contains(encode(location))) return crate;
        }
        return null;
    }

    public void saveDisplayName(Crate crate) {
        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getCratesTable() + " SET DISPLAY=? WHERE ID=?");

            statement.setString(1, crate.getDisplayName());
            statement.setString(2, crate.getId().toString());

            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveKeyItem(Crate crate) {
        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getCratesTable() + " SET KEY_ITEM=? WHERE ID=?");

            statement.setString(1, DevRoomCrates.getInstance().getRewardManager().encode(crate.getKey(), true));
            statement.setString(2, crate.getId().toString());

            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encode(Location location) {
        return "world=" + location.getWorld().getName() + ",x=" + location.getBlockX() + ",y=" + location.getBlockY() + ",z=" + location.getBlockZ();
    }

    public Location decode(String string) {
        String[] strs = string.split(",");
        World world = Bukkit.getWorld(strs[0].split("=")[1]);
        int x = Integer.parseInt(strs[1].split("=")[1]);
        int y = Integer.parseInt(strs[2].split("=")[1]);
        int z = Integer.parseInt(strs[3].split("=")[1]);
        return new Location(world, x, y, z);
    }

    public void deleteCrate(Crate crate) {

        try {

            PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getCratesTable() + " WHERE ID=?");

            statement.setString(1, crate.getId().toString());

            crates.remove(crate);
            crate.deleteLocations();
            crate.deleteRewards();

            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Crate getFromDisplay(ItemStack displayItem) {
        if (!displayItem.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(DevRoomCrates.getInstance(), "crate-id");
        ItemMeta itemMeta = displayItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, new UUIDDataType())) {
            return getCrate(container.get(key, new UUIDDataType()));
        }
        return null;
    }

    public ItemStack defaultKey(Crate crate) {

        ItemStack itemStack = ItemBuilder.build(Material.TRIPWIRE_HOOK, 1, crate.getDisplayName() + " &bKey", Arrays.asList(
                "&8&m&l-----------------",
                "&7Right click on a " + crate.getDisplayName() + "&7 crate to use.",
                "&8&m&l-----------------"
        ));

        NamespacedKey key = new NamespacedKey(DevRoomCrates.getInstance(), "crate-id");
        ItemMeta meta = itemStack.getItemMeta();

        meta.getPersistentDataContainer().set(key, new UUIDDataType(), crate.getId());

        itemStack.setItemMeta(meta);

        return itemStack;

    }


}
