package me.austin.devroomcrates.commands.subcommands;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CreateSubCommand extends DevRoomCommand {
    public CreateSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = DevRoomCrates.getInstance().getChat();

        // /crate create <display>

        if(args.length == 1) {
            chat.usage(sender, "/crate create <display>");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for(int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String display = chat.format(sb.toString().trim());

        Crate crate = DevRoomCrates.getInstance().getCrateManager().createCrate(UUID.randomUUID(), display);

        chat.sendMessage(sender, "&fYou have created the '" + crate.getDisplayName() + "&f' crate.");

    }
}
