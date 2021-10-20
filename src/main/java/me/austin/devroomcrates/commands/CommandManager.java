package me.austin.devroomcrates.commands;

import lombok.Getter;
import me.austin.devroomcrates.commands.subcommands.*;
import me.austin.devroomcrates.utils.DevRoomCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    @Getter
    private static CommandManager instance;
    @Getter
    private final Map<String, DevRoomCommand> commands = new HashMap<>();

    public CommandManager() {

        instance = this;

        commands.put("help", new HelpSubCommand("help", "drcrates.help", "/crates help", "Displays useful information about the plugin."));
        commands.put("create", new CreateSubCommand("create", "drcrates.create", "/crates create", "Create a new Crate type."));
        commands.put("edit", new EditSubCommand("edit", "drcrates.edit", "/crates edit", "Edit the crate types."));
        commands.put("place", new PlaceSubCommand("place", "drcrates.place", "/crates place", "Place a crate."));
        commands.put("key", new KeySubCommand("key", "drcrates.key", "/crates key", "Give crate keys."));
        commands.put("top", new TopSubCommand("top", "drcrates.top", "/crates top", "Check top Crate Openers."));

    }

    public DevRoomCommand find(String command) {
        return commands.get(command);
    }


}
