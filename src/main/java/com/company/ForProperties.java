package com.company;

import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ForProperties {
    public static final String ARG_START = "-pathConfig=";

    private static final String PROPERTY_IP_SERVER_1 = "ip_server_1";
    private static final String PROPERTY_PORT_DO_SERVER_1 = "port_server_1";
    private static final String PROPERTY_URL_FOR_SQL = "url_for_sql";
    private static final String PROPERTY_IS_RASPBERRY_PI = "is_raspberry_pi";
    private static final String PROPERTY_DEVICE_ID = "device_id";
    private static final String PROPERTY_PERIOD_SEC = "period";

    private String ip;
    private int port;
    private String urlForDb;
    private Boolean isRpi;  // Is this client working on Raspberry Pi
    private int deviceId;
    private int period;  // Period sending data (in seconds)

    boolean load(String arg, Logger logger) {
        Properties prop = new Properties();

        String path = arg.substring(ARG_START.length());

        try(InputStream inputStream = new FileInputStream(path))  {
            prop.load(inputStream);

            // port
            try {
                port = Integer.parseInt(prop.getProperty(PROPERTY_PORT_DO_SERVER_1));
            } catch (NumberFormatException e) {
                logger.error("can't parse properties: can't read port", e);
                return false;
            }
            logger.info("Port " + port);

            // 3. Db
            urlForDb = prop.getProperty(PROPERTY_URL_FOR_SQL);
            logger.info("Url for postgresql" + urlForDb);

        } catch(IOException e){
            logger.error("can't read properties", e);
            return false;
        }

        return true;
    }


    private Boolean readIsRpi(String isRpiStr, Logger logger) {
        if (null == isRpiStr) {
            return null;
        }

        switch (isRpiStr) {
            case "true":
                return true;
            case "false": isRpi = false;
                return false;
            default:
                logger.error(
                        "can't parse boolean parameter - isRpi. Valid values: \"true\" and \"false\". Actual value: "
                                + isRpiStr);
                return null;
        }
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getUrlForDb() {
        return urlForDb;
    }

    public boolean getIsRpi() {
        return isRpi;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getPeriod() {
        return period;
    }
}
