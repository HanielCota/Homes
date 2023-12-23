package com.github.hanielcota.homes.infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HikariCPDataSource {

    private static final HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig("/hikari.properties");
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            log.error("Failed to initialize HikariCPDataSource", e);
            throw new RuntimeException("Failed to initialize HikariCPDataSource", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            log.error("DataSource is null. Unable to get connection.");
            throw new SQLException("DataSource is null. Unable to get connection.");
        }
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource == null) {
            log.warn("DataSource is null. No action taken.");
            return;
        }

        if (dataSource.isClosed()) {
            log.warn("DataSource is already closed. No action taken.");
            return;
        }

        dataSource.close();
        log.info("DataSource closed successfully.");
    }
}
