package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Db {
    static Logger logger = LogManager.getLogger(Db.class.getName());

    private final String url;


    Db(String url) {
        this.url = url;
    }

    private static final String SQL_IS_ADDED_CHAT_ID =
            "SELECT is_admin FROM chat_ids WHERE id = ?";
    Optional<Boolean> isAdmin(Long chatId) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(SQL_IS_ADDED_CHAT_ID)) {

            statement.setLong(1, chatId);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getBoolean("is_admin"));
                } else {
                    return Optional.empty();
                }
            } catch (SQLException e) {
                logger.error(e);
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error(e);
            return Optional.empty();
        }
    }

    private static final String SQL_GET_ALL_USERS = "SELECT id FROM chat_ids";
    List<Long> getAllUsers() {

        List<Long> chatIds = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery(SQL_GET_ALL_USERS)) {

            while (set.next()) {
                chatIds.add(set.getLong("id"));
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return chatIds;
    }

    private static final String SQL_INSERT_CHAT_ID =
            "INSERT INTO chat_ids (id, is_admin) VALUES(?, FALSE)";
    boolean insertUser(Long chatId) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_CHAT_ID)) {

            statement.setLong(1,chatId);
            statement.executeUpdate();

        } catch (SQLException e) {
            logger.error("can't insert user, chat_id = " + chatId, e);
            return false;
        }

        return true;
    }
}
