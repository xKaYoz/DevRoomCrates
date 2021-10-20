package me.austin.devroomcrates.users;

import lombok.Getter;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {

    @Getter
    private final List<User> users;
    private final Chat chat;
    private final MySQL mySQL;

    public UserManager() {
        users = new ArrayList<>();
        chat = DevRoomCrates.getInstance().getChat();
        mySQL = DevRoomCrates.getInstance().getMySQL();
    }

    public void loadUsers() {

        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getUsersTable());

            ResultSet rs = statement.executeQuery();

            int loaded = 0;

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString(1));
                int opened = rs.getInt(2);
                if (!isLoaded(uuid)) {
                    User user = new User(uuid, opened);
                    users.add(user);
                    loaded++;
                }
            }

            statement.close();
            chat.log("Loaded " + loaded + " users.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public User loadUser(UUID uuid) {
        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getUsersTable() + " WHERE UUID=?");

            statement.setString(1, uuid.toString());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int opened = rs.getInt(2);
                if (!isLoaded(uuid)) {
                    User user = new User(uuid, opened);
                    users.add(user);
                    statement.close();
                    return user;
                } else {
                    return getUser(uuid);
                }
            }

            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return createUser(uuid);
    }

    public User createUser(UUID uuid) {

        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getUsersTable() + " (UUID,OPENED) VALUES (?,?)");

            statement.setString(1, uuid.toString());
            statement.setInt(2, 0);

            statement.executeUpdate();
            statement.close();

            return new User(uuid, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUser(User user) {

        try {
            PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getUsersTable() + " SET OPENED=? WHERE UUID=?");

            statement.setInt(1, user.getCratesOpened());
            statement.setString(2, user.getUuid().toString());

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveUsers() {
        int saved = 0;
        for(User user : users) {
            saveUser(user);
            saved++;
        }
        chat.log("Saved " + saved + " users.");
    }

    public boolean isLoaded(UUID uuid) {
        return getUser(uuid) != null;
    }

    public User getUser(UUID uuid) {
        for(User user : users) {
            if(user.getUuid().toString().equals(uuid.toString())) return user;
        }
        return null;
    }

}
