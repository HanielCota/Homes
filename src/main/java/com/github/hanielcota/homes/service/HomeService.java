package com.github.hanielcota.homes.service;

import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.repository.HomeRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

    public void createHome(
            String playerName,
            String homeName,
            String worldName,
            double x,
            double y,
            double z,
            double yaw,
            double pitch,
            boolean isPublic) {
        Home home = new Home(playerName, homeName, worldName, x, y, z, yaw, pitch, isPublic);
        homeRepository.saveHome(home);
    }

    public Home getHome(String playerName, String homeName) {
        return homeRepository.getHome(playerName, homeName);
    }

    public List<Home> getAllHomes(String playerName) {
        return homeRepository.getAllHomes(playerName);
    }

    public void deleteHome(String playerName, String homeName) {
        homeRepository.deleteHome(playerName, homeName);
    }

    public boolean isHomeNameTaken(String playerName, String homeName) {
        return homeRepository.isHomeNameTaken(playerName, homeName);
    }

    public List<Home> getPublicHomes(String playerName) {
        return homeRepository.getPublicHomes(playerName);
    }

    public void setHomeVisibility(String playerName, String homeName, boolean isPublic) {
        homeRepository.setHomeVisibility(playerName, homeName, isPublic);
    }
}