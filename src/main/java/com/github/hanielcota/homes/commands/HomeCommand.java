package com.github.hanielcota.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.github.hanielcota.homes.controller.HomeController;
import com.github.hanielcota.homes.domain.Home;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("home")
@AllArgsConstructor
public class HomeCommand extends BaseCommand {

    private final HomeController homeController;

    @Default
    public void onCommand(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§cUso incorreto. Utilize /home <nome da home>");
            return;
        }

        String homeName = args[0];
        if (homeName == null || homeName.isBlank()) {
            player.sendMessage("§cNome da home inválido.");
            return;
        }

        Home home = homeController.getHome(player.getName(), homeName);
        if (home == null) {
            player.sendMessage("§cHome não encontrada!");
            return;
        }

        homeController.teleportToHome(player, home);
        player.sendMessage("§aTeleportado para a home com sucesso!");
    }

    @CommandCompletion("@playerHomes")
    public List<String> onTabComplete(Player player, String[] args) {
        return homeController.getAllHomes(player.getName()).stream()
                .map(Home::getHomeName)
                .collect(Collectors.toList());
    }
}
