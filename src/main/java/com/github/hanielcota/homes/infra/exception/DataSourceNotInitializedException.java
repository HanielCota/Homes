package com.github.hanielcota.homes.infra.exception;

import java.sql.SQLException;

public class DataSourceNotInitializedException extends SQLException {

    public DataSourceNotInitializedException(String message) {
        super(message);
    }
}
