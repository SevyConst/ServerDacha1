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

}
