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
    Optional<Boolean> isAdmin(Long chatId) throws BusinessLogicException {
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
                throw new BusinessLogicException(e);
            }

        } catch (SQLException e) {
            logger.error(e);
            throw new BusinessLogicException(e);
        }
    }

    private static final String SQL_GET_ALL_USERS = "SELECT id FROM chat_ids";
    private static final String SQL_GET_ADMINS = "SELECT id FROM chat_ids WHERE is_admin = true";

    List<Long> getChatIds(boolean onlyAdmins) {

        List<Long> chatIds = new ArrayList<>();
        String sqlQuery = onlyAdmins ? SQL_GET_ADMINS : SQL_GET_ALL_USERS;

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery(sqlQuery)) {

            while (set.next()) {
                chatIds.add(set.getLong("id"));
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return chatIds;
    }

    private static final String SQL_INSERT_CHAT_ID =
            "INSERT INTO chat_ids (id, is_admin) VALUES(?, ?)";
    int insertUser(Long chatId, boolean isAdmin) throws BusinessLogicException {
        int numberInsertedUsers;
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_CHAT_ID)) {

            statement.setLong(1,chatId);
            statement.setBoolean(2, isAdmin);
            numberInsertedUsers = statement.executeUpdate();

        } catch (SQLException e) {
            logger.error(e);
            throw new BusinessLogicException("can't insert user, chat_id = "+ chatId, e);
        }

        return numberInsertedUsers;
    }

    private static final String SQL_UPDATE_ADMIN_TO_REGULAR_USER =
            "UPDATE chat_ids SET is_admin = ? WHERE id = ?";
    public int updatePrivileges(Long chatId, boolean isAdmin) throws BusinessLogicException {
        int numberUpdatedUsers;
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_ADMIN_TO_REGULAR_USER)) {

            statement.setBoolean(1, isAdmin);
            statement.setLong(2, chatId);
            numberUpdatedUsers = statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new BusinessLogicException("can't update privileges", e);
        }

        return numberUpdatedUsers;
    }

    private static final String SQL_DELETE_USER =
            "DELETE FROM chat_ids WHERE id = ?";
    public int deleteUser(Long chatId) throws BusinessLogicException {
        int numberDeletedUsers;
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_USER)) {

            statement.setLong(1, chatId);
            numberDeletedUsers = statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new BusinessLogicException("Can't delete user", e);
        }

        return numberDeletedUsers;
    }

}
