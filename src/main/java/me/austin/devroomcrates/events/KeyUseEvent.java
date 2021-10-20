package me.austin.devroomcrates.events;

import com.google.common.eventbus.DeadEvent;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.users.User;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.InventoryHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class KeyUseEvent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Chat chat = DevRoomCrates.getInstance().getChat();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if(e.getClickedBlock() == null) return;
        Location loc = e.getClickedBlock().getLocation();
        if(DevRoomCrates.getInstance().getCrateManager().getCrateAtLocation(loc) == null) return;
        Crate crate = DevRoomCrates.getInstance().getCrateManager().getCrateAtLocation(loc);
        e.setCancelled(true);
        User user = DevRoomCrates.getInstance().getUserManager().getUser(p.getUniqueId());
        if(user.isOpening()) {
            if(user.getOpeningCrate() == crate)
                p.openInventory(user.getOpeningInventory());
            else
                chat.sendMessage(p, "&cYou are currently opening a &r" + user.getOpeningCrate().getDisplayName() + "&c crate.");
            return;
        }

        if(!DevRoomCrates.getInstance().getCrateManager().isKey(hand, crate)) {
            chat.sendMessage(p, "&cYou must be holding a &r" + crate.getDisplayName() + "&c key to open this crate.");
            Vector v = loc.add(.5, .5, .5).subtract(p.getLocation()).toVector();
            v.setY(.1);
            v.normalize().multiply(-1);
            p.setVelocity(v);
            return;
        }

        chat.sendMessage(p, "Opening...");
        InventoryHandler.removeItemFromPlayer(p, hand, 1, true);
        user.openCrate(crate);


    }

}
