package com.github.hanielcota.homes.repository;

import com.github.hanielcota.homes.domain.Home;

import java.util.List;

public interface HomeRepository {
    void saveHome(Home home);

    Home getHome(String playerName, String homeName);

    List<Home> getAllHomes(String playerName);

    void deleteHome(String playerName, String homeName);

    boolean isHomeNameTaken(String playerName, String homeName);

}
