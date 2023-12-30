package com.github.hanielcota.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.github.hanielcota.homes.HomesPlugin;
import com.github.hanielcota.homes.controller.HomeController;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@CommandAlias("setHome")
@AllArgsConstructor
public class SetHomeCommand extends BaseCommand {

    private final HomeController homeController;
    private final HomesPlugin plugin;

    @Default
    public void onCommand(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§cUso incorreto. Utilize /sethome <nome da home>");
            return;
        }

        String homeName = args[0];
        if (homeName == null || homeName.isEmpty()) {
            player.sendMessage("§cNome da home inválido(a).");
            return;
        }

        if (plugin.getHomeRepository().isHomeNameTaken(player.getName(), homeName)) {
            player.sendMessage("§cJá existe uma home com o nome '" + homeName + "'. Escolha outro nome.");
            return;
        }

        homeName = homeName.substring(0, 1).toUpperCase() + homeName.substring(1);

        String worldName = player.getWorld().getName();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        double yaw = player.getLocation().getYaw();
        double pitch = player.getLocation().getPitch();

        homeController.createHome(player.getName(), homeName, worldName, x, y, z, yaw, pitch, false);

        player.sendMessage("§aHome setada com sucesso!");
    }
}
