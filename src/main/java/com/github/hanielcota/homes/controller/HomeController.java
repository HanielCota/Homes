package com.github.hanielcota.homes.controller;

import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.service.HomeService;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class HomeController {

    private final HomeService homeService;

    public void createHome(String playerName, String homeName, String worldName, double x, double y, double z, double yaw, double pitch) {
        homeService.createHome(playerName, homeName, worldName, x, y, z, yaw, pitch);
    }

    public Home getHome(String playerName, String homeName) {
        return homeService.getHome(playerName, homeName);
    }

    public List<Home> getAllHomes(String playerName) {
        return homeService.getAllHomes(playerName);
    }

    public void deleteHome(String playerName, String homeName) {
        homeService.deleteHome(playerName, homeName);
    }

    public void teleportToHome(Player player, Home home) {
        if (home == null) {
            player.sendMessage("§cHome não encontrada.");
            return;
        }

        World bukkitWorld = Bukkit.getWorld(home.getWorldName());
        if (bukkitWorld == null) {
            player.sendMessage("§cMundo '" + home.getWorldName() + "' não encontrado.");
            return;
        }

        Location homeLocation = new Location(
                bukkitWorld,
                home.getX(),
                home.getY(),
                home.getZ(),
                (float) home.getYaw(),
                (float) home.getPitch());

        player.teleportAsync(homeLocation);
    }


}