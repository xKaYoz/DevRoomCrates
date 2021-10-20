package me.austin.devroomcrates.commands.subcommands;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.guis.CratePlaceGUI;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaceSubCommand extends DevRoomCommand {

    public PlaceSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = DevRoomCrates.getInstance().getChat();

        if(sender instanceof Player) {

            Player p = (Player) sender;

            if(p.getTargetBlock(10) == null || p.getTargetBlock(10).getType() != Material.CHEST) {
                chat.sendMessage(p, "&cYou must be looking at the chest you want to make a crate.");
                return;
            }

            Location crateLoc = p.getTargetBlock(10).getLocation();

            if(DevRoomCrates.getInstance().getCrateManager().getCrateAtLocation(crateLoc) != null) {
                chat.sendMessage(p, "&cThere is already a '" +
                        DevRoomCrates.getInstance().getCrateManager().getCrateAtLocation(crateLoc).getDisplayName() + "&c' crate there.");
                return;
            }

            p.openInventory(new CratePlaceGUI(crateLoc, 1).getInventory());

        }
    }
}
