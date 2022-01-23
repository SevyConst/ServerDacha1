package com.company;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Db {
    private final String url;
    private final Logger logger;

    Db(String url, Logger logger) {
        this.url = url;
        this.logger = logger;
    }

    private static final String SQL_INSERT_EVENT = "INSERT INTO events (name_event, time_event) VALUES(?, ?)";
    void insertEvent(String nameEvent, long time) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_EVENT)) {

            statement.setString(1, nameEvent);
            statement.setLong(2, time);
            statement.executeUpdate();

        } catch (SQLException e) {
            logger.error("can't insert start event to sqlite", e);
        }
    }

}
