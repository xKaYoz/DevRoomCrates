package me.austin.devroomcrates;

import lombok.Getter;
import me.austin.devroomcrates.commands.CommandManager;
import me.austin.devroomcrates.commands.CrateCommand;
import me.austin.devroomcrates.crates.CrateManager;
import me.austin.devroomcrates.events.EditorGUIEvents;
import me.austin.devroomcrates.events.KeyUseEvent;
import me.austin.devroomcrates.rewards.RewardManager;
import me.austin.devroomcrates.users.UserEvents;
import me.austin.devroomcrates.users.UserManager;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.GUIManager;
import me.austin.devroomcrates.utils.MySQL;
import me.austin.devroomcrates.utils.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DevRoomCrates extends JavaPlugin {

    @Getter
    private static DevRoomCrates instance;
    @Getter
    private Chat chat;
    @Getter
    private MySQL mySQL;
    @Getter
    private UserManager userManager;
    @Getter
    private RewardManager rewardManager;
    @Getter
    private CrateManager crateManager;
    @Getter
    private NameManager nameManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        chat = new Chat(this, getConfig().getString("Prefix"));
        mySQL = new MySQL();
        mySQL.setup();
        userManager = new UserManager();
        rewardManager = new RewardManager();
        crateManager = new CrateManager();
        nameManager = new NameManager();
        new CommandManager();

        crateManager.loadCrates();

        userManager.loadUsers();

        loadListeners();
        loadCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        userManager.saveUsers();
    }

    private void loadCommands() {
        this.getCommand("crates").setExecutor(new CrateCommand());
    }


    private void loadListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new UserEvents(), this);
        pm.registerEvents(new GUIManager(), this);
        pm.registerEvents(new EditorGUIEvents(), this);
        pm.registerEvents(new KeyUseEvent(), this);
    }

}
