package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@ConfigurationProperties
public class ProcessingProperties {
    static Logger logger = LogManager.getLogger(ProcessingProperties.class.getName());

    private static final String PROPERTY_PORT = "port";
    private static final String PROPERTY_URL_FOR_DB = "url_for_db";
    private static final String PROPERTY_PERIOD_SENT = "period_ping";  // in seconds
    private static final String PROPERTY_TELEGRAM_BOT_TOKEN = "telegram_bot_token";
    private static final String PROPERTY_CODE_WORD = "code_word";

    private Integer port;
    private String urlForDb;
    private Integer periodPing;
    private String telegramBotToken;
    private String codeWord;

    public ProcessingProperties(@Value("${pathConfig}") String pathConfig) {
        Properties prop = new Properties();

        try(InputStream inputStream = new FileInputStream(pathConfig))  {
            prop.load(inputStream);
            
            // Db
            urlForDb = prop.getProperty(PROPERTY_URL_FOR_DB);
            logger.info("Url for postgresql: " + urlForDb);

            try {
                periodPing = Integer.parseInt(prop.getProperty(PROPERTY_PERIOD_SENT));
            } catch (NumberFormatException e) {
                logger.error("can't parse properties: can't read period", e);
                return;
            }

            telegramBotToken = prop.getProperty(PROPERTY_TELEGRAM_BOT_TOKEN);

            codeWord = prop.getProperty(PROPERTY_CODE_WORD);

        } catch(IOException e){
            logger.error("can't read properties", e);
        }
    }

    public String getUrlForDb() {
        return urlForDb;
    }

    public Integer getPeriodPing() {
        return periodPing;
    }

    String getTelegramBotToken() {
        return telegramBotToken;
    }

    String getCodeWord() { return codeWord; }
}
