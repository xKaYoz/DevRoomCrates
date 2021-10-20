package me.austin.devroomcrates.utils;

import lombok.Getter;
import lombok.Setter;
import me.austin.devroomcrates.DevRoomCrates;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQL {

    @Getter
    @Setter
    private Connection connection;
    @Getter
    private boolean connected;
    @Getter
    private String host, database, username, password, cratesTable, usersTable, locationsTable, rewardsTable;
    @Getter
    private int port;
    Plugin plugin = DevRoomCrates.getInstance();
    Chat chat = DevRoomCrates.getInstance().getChat();

    public boolean setup() {
        if(!connected) {
            FileConfiguration config = plugin.getConfig();

            host = config.getString("MySQL.Connection.Host");
            database = config.getString("MySQL.Connection.Database");
            username = config.getString("MySQL.Connection.Username");
            password = config.getString("MySQL.Connection.Password");
            port = config.getInt("MySQL.Connection.Port");
            cratesTable = config.getString("MySQL.Tables.Crates");
            usersTable = config.getString("MySQL.Tables.Users");
            locationsTable = config.getString("MySQL.Tables.Locations");
            rewardsTable = config.getString("MySQL.Tables.Rewards");

            chat.log("---------- Akarian Core MySQL Manager ----------");
            chat.log("");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                chat.log("Connecting to the MySQL database...");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
                chat.log("");
                chat.log(plugin.getName() + " has successfully established a connection to the MySQL database.");
                chat.log("");
                chat.log("Checking Tables...");


                if (checkTable(cratesTable, "ID varchar(255) NOT NULL PRIMARY KEY, DISPLAY varchar(255) NOT NULL, KEY_ITEM TEXT(65535) NOT NULL"))
                    chat.log("Crates table is checked.");
                else
                    chat.log("!! Crates table failed check !!");

                if (checkTable(usersTable, "UUID varchar(255) NOT NULL PRIMARY KEY, OPENED INT NOT NULL"))
                    chat.log("Users table is checked.");
                else
                    chat.log("!! Users table failed check !!");

                if (checkTable(locationsTable, "LOCATION varchar(255) NOT NULL PRIMARY KEY, CRATE_ID varchar(255) NOT NULL"))
                    chat.log("Locations table is checked.");
                else
                    chat.log("!! Locations table failed check !!");

                if (checkTable(rewardsTable, "ID varchar(255) NOT NULL PRIMARY KEY, CRATE_ID varchar(255) NOT NULL, ITEM_STACK TEXT(65535) NOT NULL, WEIGHT INT NOT NULL"))
                    chat.log("Rewards table is checked.");
                else
                    chat.log("!! Rewards table failed check !!");


                chat.log("");
                chat.log("Starting connection timer.");
                startConnectionTimer();
                chat.log("---------------------------------------------");
                return connected = true;
            } catch (Exception e) {
                if (e.getCause() == null) return false;
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, chat.format("&c&lAn error has occurred while connecting to the database. Please see stacktrace above."));
                chat.log("");
                chat.log("---------------------------------------------");
                chat.alert("&c&l" + plugin.getName() + " has encountered an error connecting to the MySQL database. Please check console. E" + e.getCause().getLocalizedMessage());
                return connected = false;
            }
        }
        return true;
    }

    public boolean checkTable(String tableName, String query){
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + query + ")");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void startConnectionTimer() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(reconnect())
                chat.log("Successfully established reconnection timer to the database.");
            else
                chat.log("Failed to establish reconnection timer.");
        }, 0, 20 * 60 * 60);
    }

    public boolean reconnect() {
        try {
            if(getConnection().isClosed()) {
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
                chat.log("Successfully reconnected to MySQL Database.");
            } else {
                chat.log("Connection to Database not closed. Not reconnecting.");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            chat.log("!! Failed to reconnect to MySQL Database.");
            return false;
        }
    }

    public boolean shutdown() {
        try {
            this.connection.close();
            chat.log("MySQL Connection has successfully shut down.");
        } catch (SQLException e) {
            e.printStackTrace();
            chat.log("!! MySQL Connection failed to shut down.");
            return false;
        }
        return true;
    }

}
