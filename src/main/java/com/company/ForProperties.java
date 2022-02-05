package com.company;

import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ForProperties {
    public static final String ARG_START = "-pathConfig=";

    private static final String PROPERTY_PORT = "port";
    private static final String PROPERTY_URL_FOR_SQL = "url_for_sql";

    private int port;
    private String urlForDb;

    boolean load(String arg, Logger logger) {
        Properties prop = new Properties();

        String path = arg.substring(ARG_START.length());

        try(InputStream inputStream = new FileInputStream(path))  {
            prop.load(inputStream);

            // port
            try {
                port = Integer.parseInt(prop.getProperty(PROPERTY_PORT));
            } catch (NumberFormatException e) {
                logger.error("can't parse properties: can't read port", e);
                return false;
            }
            logger.info("Port: " + port);

            // Db
            urlForDb = prop.getProperty(PROPERTY_URL_FOR_SQL);
            logger.info("Url for postgresql: " + urlForDb);

        } catch(IOException e){
            logger.error("can't read properties", e);
            return false;
        }

        return true;
    }

    public String getUrlForDb() {
        return urlForDb;
    }

}
