package com.github.hanielcota.homes.repository.impl;

import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.infra.HikariCPDataSource;
import com.github.hanielcota.homes.repository.HomeRepository;
import com.github.hanielcota.homes.repository.cache.HomeCacheManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class HomeRepositoryImpl implements HomeRepository {

    private final HomeCacheManager homeCacheManager;

    @Override
    public void saveHome(Home home) {
        final String query =
                "INSERT INTO homes (playerName, homeName, worldName, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = HikariCPDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, home.getPlayerName());
            preparedStatement.setString(2, home.getHomeName());
            preparedStatement.setString(3, home.getWorldName());
            preparedStatement.setDouble(4, home.getX());
            preparedStatement.setDouble(5, home.getY());
            preparedStatement.setDouble(6, home.getZ());
            preparedStatement.setDouble(7, home.getYaw());
            preparedStatement.setDouble(8, home.getPitch());

            preparedStatement.addBatch();
            preparedStatement.executeBatch();

            homeCacheManager.invalidateCaches(home.getPlayerName(), home.getHomeName());

        } catch (SQLException e) {
            log.error("Failed to save home: {}", home, e);
        }
    }

    @Override
    public Home getHome(String playerName, String homeName) {
        Home cachedHome = homeCacheManager.getHomeFromCache(playerName, homeName);

        if (cachedHome != null) {
            return cachedHome;
        }

        final String query =
                "SELECT playerName, homeName, worldName, x, y, z, yaw, pitch FROM homes WHERE playerName = ? AND homeName = ?";
        try (Connection connection = HikariCPDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, homeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Home home = new Home(
                        resultSet.getString("playerName"),
                        resultSet.getString("homeName"),
                        resultSet.getString("worldName"),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getDouble("yaw"),
                        resultSet.getDouble("pitch"));

                homeCacheManager.updateHomeCache(playerName, homeName, home);
                return home;
            }

        } catch (SQLException e) {
            log.error("Failed to retrieve home for playerName: {} and homeName: {}", playerName, homeName, e);
            return null;
        }
    }

    @Override
    public boolean isHomeNameTaken(String playerName, String homeName) {
        Boolean cachedIsTaken = homeCacheManager.isHomeNameTakenCache(playerName, homeName);

        if (cachedIsTaken != null) {
            return cachedIsTaken;
        }

        final String query = "SELECT 1 FROM homes WHERE playerName = ? AND homeName = ? LIMIT 1";
        try (Connection connection = HikariCPDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, homeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean isTaken = resultSet.next();
                homeCacheManager.updateIsHomeNameTakenCache(playerName, homeName, isTaken);
                return isTaken;
            }

        } catch (SQLException e) {
            log.error(
                    "Error checking if home name is taken for playerName: {} and homeName: {}",
                    playerName,
                    homeName,
                    e);
            return false;
        }
    }

    @Override
    public List<Home> getAllHomes(String playerName) {
        List<Home> cachedHomes = homeCacheManager.getAllHomesFromCache(playerName);

        if (cachedHomes != null) {
            return cachedHomes;
        }

        List<Home> homes = getAllHomesFromDatabase(playerName);
        homeCacheManager.updateAllHomesCache(playerName, homes);
        return homes;
    }

    private List<Home> getAllHomesFromDatabase(String playerName) {
        List<Home> homes = new ArrayList<>();
        final String query =
                "SELECT playerName, homeName, worldName, x, y, z, yaw, pitch FROM homes WHERE playerName = ?";

        try (Connection connection = HikariCPDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Home home = new Home(
                            resultSet.getString("playerName"),
                            resultSet.getString("homeName"),
                            resultSet.getString("worldName"),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z"),
                            resultSet.getDouble("yaw"),
                            resultSet.getDouble("pitch"));

                    homes.add(home);
                }
            }

        } catch (SQLException e) {
            log.error("Failed to retrieve homes for playerName: {}", playerName, e);
        }

        return homes;
    }

    @Override
    public void deleteHome(String playerName, String homeName) {
        final String query = "DELETE FROM homes WHERE playerName = ? AND homeName = ?";
        homeCacheManager.invalidateCaches(playerName, homeName);

        try (Connection connection = HikariCPDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, homeName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            log.error("Failed to delete home with playerName: {} and homeName: {}", playerName, homeName, e);
        }
    }
}
