package com.github.hanielcota.homes.menu.impl;

import com.github.hanielcota.homes.HomesPlugin;
import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.menu.HomeMenu;
import com.github.hanielcota.homes.menu.factory.MenuItemFactory;
import com.github.hanielcota.homes.utils.SimplixMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class HomesMenuImpl extends SimplixMenu implements HomeMenu {

    private final HomesPlugin plugin;

    public HomesMenuImpl(HomesPlugin plugin) {
        super(54, "Minhas homes");
        this.plugin = plugin;
    }

    @Override
    public void showHomesMenu(Player player, List<Home> homes) {
        getInventory().clear();

        int startingSlot = 10;

        for (Home home : homes) {
            if (startingSlot > 34) {
                break;
            }

            setHomeItem(startingSlot, player, home);
            startingSlot = getNextSlot(startingSlot);
        }

        fillEmptySlotsWithBarrier(10, 16);
        fillEmptySlotsWithBarrier(19, 25);
        fillEmptySlotsWithBarrier(28, 34);

        final ItemStack backItem = MenuItemFactory.createBackItem(false);
        final ItemStack orderItem = MenuItemFactory.createOrderItem();

        setItem(45, backItem, click -> player.closeInventory());
        setItem(49, orderItem, click -> organizeHomesAZ(player));

        open(player);
    }


    private void setHomeItem(int slot, Player player, Home home) {
        setItem(slot, MenuItemFactory.createHomeItem(home), click -> {
            if (click.isShiftClick()) {
                handleHomeDeletion(player, home, slot);
                return;
            }

            plugin.getHomeController().teleportToHome(player, home);
        });
    }


    private void handleHomeDeletion(Player player, Home home, int slot) {
        plugin.getHomeController().deleteHome(player.getName(), home.getHomeName());
        player.sendMessage("§aVocê deletou a home '" + home.getHomeName() + "' com sucesso.");

        getInventory().clear(slot);
        showHomesMenu(player, plugin.getHomeRepository().getAllHomes(player.getName()));
    }

    private void organizeHomesAZ(Player player) {
        List<Home> homes = plugin.getHomeRepository().getAllHomes(player.getName());

        if (homes.isEmpty()) {
            player.sendMessage("§cVocê não possui residências para organizar.");
            return;
        }

        homes.sort(Comparator.comparing(Home::getHomeName));
        showHomesMenu(player, homes);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 10f);
        player.sendMessage("§eVocê organizou suas homes em ordem alfabética.");
    }

    private int getNextSlot(int currentSlot) {
        return switch (currentSlot) {
            case 16 -> 19;
            case 25 -> 28;
            default -> currentSlot + 1;
        };
    }
}
