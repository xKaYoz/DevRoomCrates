package me.austin.devroomcrates.commands.subcommands;

import me.austin.devroomcrates.guis.CrateEditHomeGUI;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditSubCommand extends DevRoomCommand {
    public EditSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;

            p.openInventory(new CrateEditHomeGUI(1).getInventory());
        }

    }
}
