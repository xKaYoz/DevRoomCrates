package me.austin.devroomcrates.users;

import me.austin.devroomcrates.DevRoomCrates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        UserManager userManager = DevRoomCrates.getInstance().getUserManager();

        userManager.loadUser(e.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        UserManager userManager = DevRoomCrates.getInstance().getUserManager();

        userManager.saveUser(userManager.getUser(e.getPlayer().getUniqueId()));
    }

}
