package com.github.hanielcota.homes.repository.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.infra.HikariCPDataSource;
import com.github.hanielcota.homes.repository.HomeRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HomeRepositoryImpl implements HomeRepository {
    private final Cache<String, Home> homeCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    private final Cache<String, List<Home>> allHomesCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    @Override
    public void saveHome(Home home) {
        final String query =
                "INSERT INTO homes (playerName, homeName, worldName, x, y, z, yaw, pitch, isPublic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            preparedStatement.setBoolean(9, home.isPublic());
            preparedStatement.executeUpdate();

            log.info("Home saved successfully: {}", home);

            invalidateCaches(home.getPlayerName(), home.getHomeName());

        } catch (SQLException e) {
            log.error("Failed to save home: {}", home, e);
        }
    }

    @Override
    public Home getHome(String playerName, String homeName) {
        String cacheKey = buildCacheKey(playerName, homeName);
        Home cachedHome = homeCache.getIfPresent(cacheKey);
        if (cachedHome != null) {
            log.info("Retrieved home from cache: {}", cachedHome);
            return cachedHome;
        }

        final String query =
                "SELECT playerName, homeName, worldName, x, y, z, yaw, pitch, isPublic FROM homes WHERE playerName = ? AND homeName = ?";
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, homeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    log.info("Home not found for playerName: {} and homeName: {}", playerName, homeName);
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
                        resultSet.getDouble("pitch"),
                        resultSet.getBoolean("isPublic"));

                log.info("Retrieved home from database: {}", home);

                homeCache.put(cacheKey, home);
                return home;
            }

        } catch (SQLException e) {
            log.error("Failed to retrieve home for playerName: {} and homeName: {}", playerName, homeName, e);
        }
        return null;
    }

    @Override
    public boolean isHomeNameTaken(String playerName, String homeName) {
        final String query = "SELECT 1 FROM homes WHERE playerName = ? AND homeName = ? LIMIT 1";
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, homeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            log.error(
                    "Error checking if home name is taken for playerName: {} and homeName: {}",
                    playerName,
                    homeName,
                    e);
        }
        return true;
    }

    @Override
    public List<Home> getAllHomes(String playerName) {
        String cacheKey = buildCacheKey(playerName, "all");
        List<Home> cachedHomes = allHomesCache.getIfPresent(cacheKey);
        if (cachedHomes != null) {
            log.info("Retrieved homes from cache: {}", cachedHomes);
            return new ArrayList<>(cachedHomes);
        }

        List<Home> homes = getAllHomesFromDatabase(playerName);

        allHomesCache.put(cacheKey, new ArrayList<>(homes));
        return new ArrayList<>(homes);
    }

    private List<Home> getAllHomesFromDatabase(String playerName) {
        List<Home> homes = new ArrayList<>();
        final String query =
                "SELECT playerName, homeName, worldName, x, y, z, yaw, pitch, isPublic FROM homes WHERE playerName = ?";
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
                            resultSet.getDouble("pitch"),
                            resultSet.getBoolean("isPublic"));

                    homes.add(home);
                    log.info("Retrieved home: {}", home);
                }

                return new ArrayList<>(homes);
            }

        } catch (SQLException e) {
            log.error("Failed to retrieve homes for playerName: {}", playerName, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Home> getPublicHomes(String playerName) {
        String cacheKey = buildCacheKey(playerName, "public");
        List<Home> cachedPublicHomes = allHomesCache.getIfPresent(cacheKey);
        if (cachedPublicHomes != null) {
            log.info("Retrieved public homes from cache: {}", cachedPublicHomes);
            return new ArrayList<>(cachedPublicHomes);
        }

        List<Home> publicHomes = getPublicHomesFromDatabase(playerName);

        allHomesCache.put(cacheKey, new ArrayList<>(publicHomes));
        return new ArrayList<>(publicHomes);
    }

    private List<Home> getPublicHomesFromDatabase(String playerName) {
        List<Home> publicHomes = new ArrayList<>();
        final String query =
                "SELECT playerName, homeName, worldName, x, y, z, yaw, pitch, isPublic FROM homes WHERE playerName = ? AND isPublic = 1";
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
                            resultSet.getDouble("pitch"),
                            resultSet.getBoolean("isPublic"));

                    publicHomes.add(home);
                    log.info("Retrieved public home: {}", home);
                }

                return new ArrayList<>(publicHomes);
            }

        } catch (SQLException e) {
            log.error("Failed to retrieve public homes for playerName: {}", playerName, e);
        }
        return new ArrayList<>();
    }

    @Override
    public void setHomeVisibility(String playerName, String homeName, boolean isPublic) {
        final String query = "UPDATE homes SET isPublic = ? WHERE playerName = ? AND homeName = ?";
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBoolean(1, isPublic);
            preparedStatement.setString(2, playerName);
            preparedStatement.setString(3, homeName);

            preparedStatement.executeUpdate();

            log.info(
                    "Home visibility updated successfully: playerName={}, homeName={}, isPublic={}",
                    playerName,
                    homeName,
                    isPublic);

            invalidateCaches(playerName, homeName);

        } catch (SQLException e) {
            log.error(
                    "Failed to update home visibility: playerName={}, homeName={}, isPublic={}",
                    playerName,
                    homeName,
                    isPublic,
                    e);
        }
    }

    @Override
    public void deleteHome(String playerName, String homeName) {
        final String query = "DELETE FROM homes WHERE playerName = ? AND homeName = ?";
        String cacheKey = buildCacheKey(playerName, homeName);

        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, homeName);
            preparedStatement.executeUpdate();

            log.info("Home deleted successfully: {}", cacheKey);

            invalidateCaches(playerName, homeName);

        } catch (SQLException e) {
            log.error("Failed to delete home with playerName: {} and homeName: {}", playerName, homeName, e);
        }
    }

    private void invalidateCaches(String playerName, String homeName) {
        homeCache.invalidate(buildCacheKey(playerName, homeName));
        allHomesCache.invalidate(buildCacheKey(playerName, "all"));
        allHomesCache.invalidate(buildCacheKey(playerName, "public"));
    }

    private String buildCacheKey(String playerName, String homeName) {
        return playerName + ":" + homeName;
    }
}