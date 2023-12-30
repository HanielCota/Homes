package com.github.hanielcota.homes.infra.exception;

public class DataSourceInitializationException extends RuntimeException {

    public DataSourceInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
