package com.github.hanielcota.homes.menu.impl;

import com.github.hanielcota.homes.HomesPlugin;
import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.menu.HomeMenu;
import com.github.hanielcota.homes.menu.factory.MenuItemFactory;
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
        final int slotIncrement = 1;

        for (Home home : homes) {
            setItem(startingSlot, player, home);
            startingSlot += (startingSlot == 17 || startingSlot == 26) ? 4 : slotIncrement;
        }

        fillEmptySlotsWithBarrier(10, 16);
        fillEmptySlotsWithBarrier(19, 25);
        fillEmptySlotsWithBarrier(28, 34);

        setItem(50, MenuItemFactory.createPublicHomesItem(), click -> showPublicHomes(player));
        setItem(45, MenuItemFactory.createBackItem(false), click -> player.closeInventory());
        setItem(49, MenuItemFactory.createOrderItem(), click -> organizeHomesAZ(player));

        open(player);
    }

    private void setItem(int slot, Player player, Home home) {
        setItem(slot, MenuItemFactory.createHomeItem(home), click -> {
            if (click.isRightClick()) {
                handlePublicVisibilityChange(player, home);
                return;
            }

            if (click.isShiftClick()) {
                handleHomeDeletion(player, home, slot);
                return;
            }

            plugin.getHomeController().teleportToHome(player, home);
        });
    }

    private void handlePublicVisibilityChange(Player player, Home home) {
        boolean newVisibility = !home.isPublic();
        plugin.getHomeController().setHomeVisibility(player.getName(), home.getHomeName(), newVisibility);
        player.sendMessage(
                "§aHome '" + home.getHomeName() + "' agora é " + (newVisibility ? "pública" : "privada") + ".");
        showHomesMenu(player, plugin.getHomeRepository().getAllHomes(player.getName()));
    }

    private void handleHomeDeletion(Player player, Home home, int slot) {
        plugin.getHomeController().deleteHome(player.getName(), home.getHomeName());
        player.sendMessage("§cHome '" + home.getHomeName() + "' deletada.");
        getInventory().clear(slot);
        showHomesMenu(player, plugin.getHomeRepository().getAllHomes(player.getName()));
    }

    private void showPublicHomes(Player player) {
        List<Home> publicHomes = plugin.getHomeController().getPublicHomes(player.getName());
        new PublicHomesMenuImpl(player, publicHomes, plugin).open(player);
    }

    private void organizeHomesAZ(Player player) {
        List<Home> homes = plugin.getHomeRepository().getAllHomes(player.getName());
        homes.sort(Comparator.comparing(Home::getHomeName));
        showHomesMenu(player, homes);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 10f);
        player.sendMessage("§eVocê organizou suas homes em ordem alfabética.");
    }
}
