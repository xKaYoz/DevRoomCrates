package me.austin.devroomcrates.events;

import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.guis.CrateEditSelectGUI;
import me.austin.devroomcrates.guis.CrateRewardsGUI;
import me.austin.devroomcrates.guis.RewardEditGUI;
import me.austin.devroomcrates.rewards.Reward;
import me.austin.devroomcrates.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class EditorGUIEvents implements Listener {

    private static final HashMap<UUID, ItemStack> rewardAdd1 = new HashMap<>();

    @EventHandler
    public void onHit1(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Chat chat = DevRoomCrates.getInstance().getChat();

        //Add Reward
        if(CrateRewardsGUI.getAddRewardMap().containsKey(p.getUniqueId())) {
            if(e.getAction().isLeftClick() && !rewardAdd1.containsKey(p.getUniqueId())) {
                e.setCancelled(true);
                if (p.getInventory().getItemInMainHand().getType().isAir()) {
                    chat.sendMessage(p, "&cYou must be holding an item in your hand to add as a reward.");
                    return;
                }
                ItemStack itemStack = p.getInventory().getItemInMainHand();

                chat.sendMessage(p, "&fYou are adding &b" + chat.formatItem(itemStack) + "&f to " +
                        CrateRewardsGUI.getAddRewardMap().get(p.getUniqueId()).getCrate().getDisplayName() + "&f.");
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the weight you want to set the reward to.");
                rewardAdd1.put(p.getUniqueId(), itemStack);
            } else {
                Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                    p.openInventory(CrateRewardsGUI.getAddRewardMap().get(p.getUniqueId()).getInventory());
                    rewardAdd1.remove(p.getUniqueId());
                    CrateRewardsGUI.getAddRewardMap().remove(p.getUniqueId());
                });

            }
        }

        //Change crate display name cancel
        if(CrateEditSelectGUI.getDisplayMap().containsKey(p.getUniqueId()) && e.getAction().isLeftClick()) {
            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(CrateEditSelectGUI.getDisplayMap().get(p.getUniqueId()).getInventory());
                CrateEditSelectGUI.getDisplayMap().remove(p.getUniqueId());
            });
        }

        if(RewardEditGUI.getListAmount().containsKey(p.getUniqueId())) {
            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(RewardEditGUI.getListAmount().get(p.getUniqueId()).getInventory());
                RewardEditGUI.getListAmount().remove(p.getUniqueId());
            });
        }

        if(RewardEditGUI.getListWeight().containsKey(p.getUniqueId())) {
            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(RewardEditGUI.getListWeight().get(p.getUniqueId()).getInventory());
                RewardEditGUI.getListWeight().remove(p.getUniqueId());
            });
        }

    }

    @EventHandler
    public void onChat1(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = DevRoomCrates.getInstance().getChat();

        //Edit reward amount
        if(RewardEditGUI.getListAmount().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int amount;
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cThe amount must be an integer.");
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the amount you want to set the reward to.");
                return;
            }

            if (amount <= 0) {
                chat.sendMessage(p, "&cThe amount must be greater than 0.");
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the amount you want to set the reward to.");
                return;
            }

            Reward reward = RewardEditGUI.getListAmount().get(p.getUniqueId()).getReward();

            reward.getItemStack().setAmount(amount);

            DevRoomCrates.getInstance().getRewardManager().saveItemStack(reward);

            chat.sendMessage(p, "&fYou have set the amount to &e" + input + "&f.");
            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(RewardEditGUI.getListAmount().get(p.getUniqueId()).getInventory());
                RewardEditGUI.getListAmount().remove(p.getUniqueId());
            });

        }

        //Edit reward weight
        if(RewardEditGUI.getListWeight().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int weight;
            try {
                weight = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cThe weight must be an integer.");
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the weight you want to set the reward to.");
                return;
            }

            if (weight <= 0) {
                chat.sendMessage(p, "&cThe weight must be greater than 0.");
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the weight you want to set the reward to.");
                return;
            }

            Reward reward = RewardEditGUI.getListWeight().get(p.getUniqueId()).getReward();

            reward.setWeight(weight);

            DevRoomCrates.getInstance().getRewardManager().saveItemStack(reward);

            chat.sendMessage(p, "&fYou have set the weight to &e" + input + "&f.");
            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(RewardEditGUI.getListWeight().get(p.getUniqueId()).getInventory());
                RewardEditGUI.getListWeight().remove(p.getUniqueId());
            });

        }

        //Add a reward
        if(CrateRewardsGUI.getAddRewardMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if(!rewardAdd1.containsKey(p.getUniqueId())) {
                chat.sendMessage(p, "&cLeft click with the item in your hand that you want to add as a reward.");
                return;
            }
            int weight;
            try {
                weight = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cThe weight must be an integer.");
                chat.sendMessage(p, "&eLeft click to cancel");
                chat.sendMessage(p, "&eEnter the weight you want to set the reward to.");
                return;
            }

            Reward reward = DevRoomCrates.getInstance().getRewardManager().createReward(UUID.randomUUID(),
                    CrateRewardsGUI.getAddRewardMap().get(p.getUniqueId()).getCrate(), rewardAdd1.get(p.getUniqueId()), weight);

            chat.sendMessage(p, "&fYou have created the reward of &e" + chat.formatItem(reward.getItemStack()) +
                    "&fin the " + DevRoomCrates.getInstance().getCrateManager().getCrate(reward.getCrateID()).getDisplayName() +
                    "&f crate.");

            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(CrateRewardsGUI.getAddRewardMap().get(p.getUniqueId()).getInventory());
                rewardAdd1.remove(p.getUniqueId());
                CrateRewardsGUI.getAddRewardMap().remove(p.getUniqueId());
            });
        }

        //Change Crate Display Name
        if(CrateEditSelectGUI.getDisplayMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            Crate crate = CrateEditSelectGUI.getDisplayMap().get(p.getUniqueId()).getCrate();

            crate.setDisplayName(input);
            DevRoomCrates.getInstance().getCrateManager().saveDisplayName(crate);

            chat.sendMessage(p, "&fYou have set the display name to " + input + "&f.");

            Bukkit.getScheduler().runTask(DevRoomCrates.getInstance(), () -> {
                p.openInventory(CrateEditSelectGUI.getDisplayMap().get(p.getUniqueId()).getInventory());
                CrateEditSelectGUI.getDisplayMap().remove(p.getUniqueId());
            });
            //TODO Update all crates
        }
    }

}
