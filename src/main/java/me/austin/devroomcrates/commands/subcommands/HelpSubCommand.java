package me.austin.devroomcrates.commands.subcommands;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.commands.CommandManager;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpSubCommand extends DevRoomCommand {

    public HelpSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        int page = 1;
        Chat chat = DevRoomCrates.getInstance().getChat();

        if(args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                chat.sendMessage(sender, "&cThe second argument must be an integer.");
                return;
            }
        }

        List<DevRoomCommand> commands = new ArrayList<>();

        for(DevRoomCommand command : CommandManager.getInstance().getCommands().values()) {
            if(sender.hasPermission(command.getPermission())) commands.add(command);
        }

        if(commands.size() == 0) {
            chat.sendRawMessage(sender, "&8&m----------------------------------------");
            chat.sendRawMessage(sender, "&c&l  DevRoomCrates Help Menu");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&cYou do not have any DevRoomCrates permissions.");
            chat.sendRawMessage(sender, "&8&m----------------------------------------");
            return;
        }

        chat.sendRawMessage(sender, "&8&m----------------------------------------");
        chat.sendRawMessage(sender, "&c&l  DevRoomCrates Help Menu &7(" + (page) + "/" + (commands.size() % 10 == 0 ? commands.size() / 10 : (commands.size() / 10) + 1) + ")");
        chat.sendRawMessage(sender, "");
        chat.sendRawMessage(sender, "&f  <> &8- &fRequired Commands");
        chat.sendRawMessage(sender, "&7  [] &8- &fOptional Commands.");
        chat.sendRawMessage(sender, "");

        int to = page * 10;
        int from = to - 10;

        if(commands.size() >= 10) {
            for (int i = from; i < to; i++) {
                if(commands.size() == i) break;
                DevRoomCommand command = (DevRoomCommand) commands.toArray()[i];

                if(command == null) break;

                chat.sendRawMessage(sender, "  &c" + command.getUsage() + " &8- &7" + command.getDescription());

            }
        } else {
            for(DevRoomCommand command : commands) {
                chat.sendRawMessage(sender, "  &c" + command.getUsage() + " &8- &7" + command.getDescription());
            }
        }
        chat.sendRawMessage(sender, "&8&m----------------------------------------");
    }
}
