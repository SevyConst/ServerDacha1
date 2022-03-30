package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Db {
    static Logger logger = LogManager.getLogger(Db.class.getName());

    @Autowired
    ProcessingProperties processingProperties;

    void doSmth() {
        System.out.println("do smth");
    }

    void printUrl() {
        logger.info("db Url :" + processingProperties.getUrlForDb());
    }

//    private final String url;
//    private final Logger logger;

//    Db(String url, Logger logger) {
//        this.url = url;
//        this.logger = logger;
//    }


//    private static final String SQL_INSERT_EVENT =
//            "INSERT INTO events (client_id, client_event_id ,name_event, time_event) VALUES(?, ?, ?, ?)";
//    void insertEvent(int clientId, long clientEventId, String nameEvent, String timeEvent) {
//        try (Connection connection = DriverManager.getConnection(url);
//             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_EVENT)) {
//
//            statement.setLong(1, clientId);
//            statement.setLong(2, clientEventId);
//            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS Z");
//
//
//            statement.setTimestamp(1, Timestamp.valueOf(timeEvent));
//
//            statement.executeUpdate();
//
//        } catch (SQLException e) {
//            logger.error("can't insert start event to sqlite", e);
//        }
//    }
}
