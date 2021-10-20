package me.austin.devroomcrates.commands.subcommands;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.guis.CrateKeyGiveGUI;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeySubCommand extends DevRoomCommand {

    public KeySubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = DevRoomCrates.getInstance().getChat();

        if(sender instanceof Player) {

            Player p = (Player) sender;
            Player target;
            int amount;

            //Give one to self
            if(args.length == 1) {
                amount = 1;
                target = p;
            } else if(args.length == 2) {
                try {
                    amount = Integer.parseInt(args[1]);
                    target = p;
                } catch (NumberFormatException e) {
                    if(Bukkit.getPlayer(args[1]) == null) {
                        chat.sendMessage(p, "The 2nd command must be a player to give to or the amount of keys.");
                        return;
                    }
                    target = Bukkit.getPlayer(args[1]);
                    amount = 1;
                }
            } else if(args.length == 3) {
                try {
                    amount = Integer.parseInt(args[1]);
                    target = Bukkit.getPlayer(args[2]);
                } catch (NumberFormatException e) {
                    try {
                        target = Bukkit.getPlayer(args[1]);
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        chat.sendMessage(p, "The arguments must be the amount of keys to give and the player to give to.");
                        return;
                    }
                }
            } else {
                chat.usage(p, "/crate key [player/amount] [player/amount]");
                return;
            }

            if(target == null) {
                chat.sendMessage(p, "&cCannot find the target player.");
                return;
            }

            if(amount <= 0) {
                chat.sendMessage(p, "&cThe amount must be greater than 0.");
                return;
            }

            p.openInventory(new CrateKeyGiveGUI(target, amount, 1).getInventory());

        }
    }
}
