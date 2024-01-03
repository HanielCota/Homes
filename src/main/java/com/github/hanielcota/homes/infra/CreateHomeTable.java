package com.github.hanielcota.homes.infra;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.Statement;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateHomeTable {

    public static void createHomeTable() {
        try (Connection connection = HikariCPDataSource.getConnection();
             Statement statement = connection.createStatement()) {

            if (statement == null) {
                log.error("Failed to create homes table. Statement is null.");
                return;
            }

            final String createTableSQL = "CREATE TABLE IF NOT EXISTS homes ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "playerName VARCHAR(50) NOT NULL,"
                    + "homeName VARCHAR(50) NOT NULL,"
                    + "worldName VARCHAR(50) NOT NULL,"
                    + "x DOUBLE NOT NULL,"
                    + "y DOUBLE NOT NULL,"
                    + "z DOUBLE NOT NULL,"
                    + "yaw DOUBLE NOT NULL,"
                    + "pitch DOUBLE NOT NULL"
                    + ")";

            statement.executeUpdate(createTableSQL);
            log.info("Table 'homes' created successfully.");

        } catch (Exception e) {
            log.error("Failed to create homes table.", e);
        }
    }
}
