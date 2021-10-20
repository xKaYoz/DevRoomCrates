package me.austin.devroomcrates.commands;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CrateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String str, String[] args) {

        Chat chat = DevRoomCrates.getInstance().getChat();
        long start = System.currentTimeMillis();

        if (args.length == 0) {
            CommandManager.getInstance().find("help").execute(sender, args);
            log(start, sender, CommandManager.getInstance().find("help"));
            return false;
        }

        DevRoomCommand subCommand = CommandManager.getInstance().find(args[0]);

        if (subCommand == null) {

            for(String s : CommandManager.getInstance().getCommands().keySet()){
                DevRoomCommand sc = CommandManager.getInstance().getCommands().get(s);

                for(String aliases : CommandManager.getInstance().getCommands().get(s).getAliases()) {
                    if(aliases.equalsIgnoreCase(args[0])) {
                        if (sender.hasPermission(sc.getPermission())) {
                            sc.execute(sender, args);
                        } else {
                            chat.noPermission(sender);
                        }
                        log(start, sender, sc);
                        return false;
                    }
                }
            }

            chat.sendMessage(sender, "&cInvalid Command. Use /crate help for more info.");
            log(start, sender, null);
            return false;
        }

        if (sender.hasPermission(subCommand.getPermission())) {
            subCommand.execute(sender, args);
        } else {
            chat.noPermission(sender);

        }
        log(start, sender, subCommand);

        return false;
    }

    private void log(long time, CommandSender sender, DevRoomCommand command) {
        String str = command == null ? "UNKNOWN" : command.getName();
        DevRoomCrates.getInstance().getChat().log(sender.getName() + " executed " + str + " in " + (System.currentTimeMillis() - time) + "ms.");
    }
}
