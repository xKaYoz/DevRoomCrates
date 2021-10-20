package me.austin.devroomcrates.users;

import lombok.Getter;
import lombok.Setter;
import me.austin.devroomcrates.DevRoomCrates;
import me.austin.devroomcrates.crates.Crate;
import me.austin.devroomcrates.guis.OpenCrateGUI;
import me.austin.devroomcrates.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    @Getter
    private final UUID uuid;
    @Getter @Setter
    private int cratesOpened;
    @Getter @Setter
    private boolean opening;
    private Reward[] showing;
    private int openingTimer;
    @Getter
    private Crate openingCrate;
    @Getter
    private Inventory openingInventory;

    public User(UUID uuid, int cratesOpened) {
        this.uuid = uuid;
        this.cratesOpened = cratesOpened;
    }

    public void openCrate(Crate crate) {
        cratesOpened++;
        opening = true;
        openingCrate = crate;

        //Setup the showing table
        showing = new Reward[7];

        for(int i = 0; i <= 6; i++) {
            showing[i] = crate.getRandomReward();
        }

        Player p = Bukkit.getPlayer(uuid);

        openingInventory = new OpenCrateGUI(p, crate).getInventory();

        p.openInventory(openingInventory);

        setShowing();

        AtomicInteger tickCount = new AtomicInteger();

        Random r = new Random();int phase1 = r.nextInt(2) + 1;
        AtomicBoolean p1 = new AtomicBoolean(true);
        int phase2 = phase1 + r.nextInt(2) + 1;
        AtomicBoolean p2 = new AtomicBoolean(false);
        int phase3 = phase2 + r.nextInt(4) + 2;
        AtomicBoolean p3 = new AtomicBoolean(false);
        int phase4 = phase3 + r.nextInt(4) + 2;
        AtomicBoolean p4 = new AtomicBoolean(false);
        int end = phase4 + 3;
        AtomicBoolean e = new AtomicBoolean(false);

        openingTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(DevRoomCrates.getInstance(), () -> {

            if (p1.get()) {
                tick();
            } else if (p2.get()) {
                if (tickCount.get() % 5 == 0) {
                    tick();
                }
            } else if (p3.get()) {
                if (tickCount.get() % 10 == 0) {
                    tick();
                }
            } else if (p4.get()) {
                if (tickCount.get() % 20 == 0) {
                    tick();
                }
            }

            if (p1.get()) {
                if (tickCount.get() >= 20 * phase1) {
                    p1.set(false);
                    p2.set(true);
                }
            } else if (p2.get()) {
                if (tickCount.get() >= 20 * phase2) {
                    p2.set(false);
                    p3.set(true);
                }
            } else if (p3.get()) {
                if (tickCount.get() >= 20 * phase3) {
                    p3.set(false);
                    p4.set(true);
                }
            } else if (p4.get()) {
                if (tickCount.get() >= 20 * phase4) {
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    DevRoomCrates.getInstance().getChat().sendMessage(p, "&fYou have received &c" + DevRoomCrates.getInstance().getChat().formatItem(showing[3].getItemStack()));
                    p.getInventory().addItem(showing[3].getItemStack());
                    p4.set(false);
                    e.set(true);
                }
            } else if (e.get()) {
                if (tickCount.get() >= 20 * end) {
                    cancelTimer();
                    p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    opening = false;
                    openingCrate = null;
                    openingInventory = null;

                }
            }

            tickCount.incrementAndGet();

        }, 0, 1);

    }

    private void cancelTimer() {
        Bukkit.getScheduler().cancelTask(openingTimer);
    }

    public void setShowing() {

        openingCrate.calculateWeights();

        for(int t = 10; t <= 16; t++) {
            Reward reward = showing[t - 10];
            reward.setupDisplay();
            openingInventory.setItem(t, reward.getDisplay());
        }

        Bukkit.getPlayer(uuid).updateInventory();
    }

    private void tick() {
        showing[0] = showing[1];
        showing[1] = showing[2];
        showing[2] = showing[3];
        showing[3] = showing[4];
        showing[4] = showing[5];
        showing[5] = showing[6];
        showing[6] = openingCrate.getRandomReward();
        setShowing();
        Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

}
