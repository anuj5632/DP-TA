package com.fooddelivery.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {
    private static volatile LoggingService instance;
    private static final Object LOCK = new Object();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LoggingService() {
    }

    public static LoggingService getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new LoggingService();
                }
            }
        }
        return instance;
    }

    public void info(String message) {
        System.out.println("[INFO  " + now() + "] " + message);
    }

    public void warn(String message) {
        System.out.println("[WARN  " + now() + "] " + message);
    }

    public void error(String message) {
        System.out.println("[ERROR " + now() + "] " + message);
    }

    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
