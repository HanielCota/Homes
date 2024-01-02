package com.github.hanielcota.homes.menu.impl;

import com.github.hanielcota.homes.HomesPlugin;
import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.menu.factory.MenuItemFactory;
import com.github.hanielcota.homes.utils.FastInv;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class PublicHomesMenuImpl extends FastInv {

    private final HomesPlugin plugin;

    public PublicHomesMenuImpl(Player player, List<Home> publicHomes, HomesPlugin plugin) {
        super(54, "Homes Públicas");
        this.plugin = plugin;

        initializeItems(player, publicHomes);
        open(player);
    }

    private void initializeItems(Player player, List<Home> publicHomes) {
        int slot = 10;

        for (Home home : publicHomes) {
            setItem(slot, MenuItemFactory.createPublicHomeItem(home), click -> teleportToPublicHome(player, home));
            slot += (slot == 17 || slot == 26) ? 4 : 1;
        }

        fillEmptySlotsWithBarrier(10, 16);
        fillEmptySlotsWithBarrier(19, 25);
        fillEmptySlotsWithBarrier(28, 34);

        setItem(45, MenuItemFactory.createBackItem(true), click -> showHomesMenu(player));
        setItem(49, MenuItemFactory.createInformationPublicItem(), click -> showInformation(player));
    }

    private void showInformation(Player player) {
        player.sendMessage(
                "",
                "§aPara tornar uma home pública, siga os passos abaixo:",
                "",
                "§7Passo 1: Abra o menu e selecione a home que deseja tornar pública.",
                "§7Passo 2: Clique com o botão direito na sua home.",
                "§7Passo 3: Segure a tecla Shift enquanto clica.",
                "",
                "§7Isso fará com que a home seja visível para outros jogadores.");
    }

    private void teleportToPublicHome(Player player, Home home) {
        plugin.getHomeController().teleportToHome(player, home);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 10f);
    }

    private void showHomesMenu(Player player) {
        new HomesMenuImpl(plugin)
                .showHomesMenu(player, plugin.getHomeController().getAllHomes(player.getName()));
    }
}
