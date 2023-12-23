package com.github.hanielcota.homes.menu.impl;

import com.github.hanielcota.homes.HomesPlugin;
import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.menu.HomeMenu;
import com.github.hanielcota.homes.utils.FastInv;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

public class HomesMenuImpl extends FastInv implements HomeMenu {

    private final HomesPlugin plugin;

    public HomesMenuImpl(HomesPlugin plugin) {
        super(54, "Minhas homes");
        this.plugin = plugin;
    }

    @Override
    public void showHomesMenu(Player player, List<Home> homes) {
        int startingSlot = 10;
        int slotIncrement = 1;

        for (Home home : homes) {
            setHomeItem(player, startingSlot, home);
            startingSlot += (startingSlot == 17 || startingSlot == 26) ? 4 : slotIncrement;
        }

        fillEmptySlotsWithBarrier(10, 16);
        fillEmptySlotsWithBarrier(19, 25);
        fillEmptySlotsWithBarrier(28, 34);

        setOrderItem(player);
        setBackItem(player);
        open(player);
    }

    private void setHomeItem(Player player, int slot, Home home) {
        setItem(slot, MenuItemFactory.createHomeItem(home), click -> {
            if (click.isShiftClick()) {
                plugin.getHomeController().deleteHome(player.getName(), home.getHomeName());
                player.sendMessage("§aHome '" + home.getHomeName() + "' deleted.");

                getInventory().clear(slot);

                showHomesMenu(player, plugin.getHomeRepository().getAllHomes(player.getName()));
                return;
            }

            plugin.getHomeController().teleportToHome(player, home);
        });
    }


    private void setBackItem(Player player) {
        int backSlot = 45;
        setItem(backSlot, MenuItemFactory.createBackItem(), click -> player.closeInventory());
    }

    private void setOrderItem(Player player) {
        setItem(49, MenuItemFactory.createOrderItem(), click -> organizeHomesAZ(player));
    }

    public void organizeHomesAZ(Player player) {
        List<Home> homes = plugin.getHomeRepository().getAllHomes(player.getName());

        homes.sort(Comparator.comparing(Home::getHomeName));

        showHomesMenu(player, homes);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 10f);
    }

    private void fillEmptySlotsWithBarrier(int startSlot, int endSlot) {
        for (int emptySlot = startSlot; emptySlot <= endSlot; emptySlot++) {
            if (getInventory().getItem(emptySlot) == null) {
                setItem(emptySlot, MenuItemFactory.createBarrierItem());
            }
        }
    }
}
