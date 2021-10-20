package me.austin.devroomcrates.commands.subcommands;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.users.User;
import me.austin.devroomcrates.utils.Chat;
import me.austin.devroomcrates.utils.DevRoomCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TopSubCommand extends DevRoomCommand {

    public TopSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = DevRoomCrates.getInstance().getChat();

        if(sender instanceof Player) {

            Player p = (Player) sender;

            chat.sendRawMessage(p, "&8&m&l---------&r  &3&lTop 10 Crate Openers  &8&m&l---------");
            chat.sendRawMessage(p, "");

            HashMap<User, Integer> list = sortedList();
            User[] users = list.keySet().toArray(new User[10]);

            for(int i = 0; i <= 10; i++) {

                if(list.size() == i) break;

                String name = users[i].getUuid().toString().equals(p.getUniqueId()) ?
                        "&e" + DevRoomCrates.getInstance().getNameManager().getName(users[i].getUuid()) :
                                "&7" + DevRoomCrates.getInstance().getNameManager().getName(users[i].getUuid());

                chat.sendRawMessage(p, "&e" + i + 1 + ". " + name + " &8-&7 " + users[i].getCratesOpened());
            }

            chat.sendRawMessage(p, "");
            chat.sendRawMessage(p, "&8&m&l----------------------------------------");

        }

    }

    public HashMap<User, Integer> sortedList() {
        HashMap<User, Integer> map = new HashMap<>();

        for(User user : DevRoomCrates.getInstance().getUserManager().getUsers()) {
            map.put(user, user.getCratesOpened());
        }

        return sort(map);
    }

    private HashMap sort(HashMap<User, Integer> map) {
        List list = new LinkedList(map.entrySet());
        /**if(!sortBool) {
            // Defined Custom Comparator here
            list.sort(new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map.Entry) (o1)).getValue())
                            .compareTo(((Map.Entry) (o2)).getValue());
                }
            });

            // Here I am copying the sorted list in HashMap
            // using LinkedHashMap to preserve the insertion order
            HashMap sortedHashMap = new LinkedHashMap();
            for (Iterator it = list.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                sortedHashMap.put(entry.getKey(), entry.getValue());
            }
            return sortedHashMap;
        } else {

        }*/

        // Defined Custom Comparator here
        list.sort(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
