package com.github.hanielcota.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.github.hanielcota.homes.menu.impl.HomesMenuImpl;
import org.bukkit.entity.Player;

import com.github.hanielcota.homes.controller.HomeController;
import com.github.hanielcota.homes.domain.Home;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
@CommandAlias("homes")
public class HomesCommand extends BaseCommand {

    private final HomeController homeController;
    private final HomesMenuImpl homesMenu;

    @Default
    public void onCommand(Player player) {
        List<Home> homes = homeController.getAllHomes(player.getName());

        if (homes.isEmpty()) {
            player.sendMessage("§cVocê não tem nenhuma home.");
            return;
        }

        homesMenu.showHomesMenu(player, homes);
        player.sendMessage("§aMenu de homes aberto!");
    }
}
