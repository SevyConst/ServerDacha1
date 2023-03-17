package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBot extends TelegramLongPollingBot {

    static Logger logger = LogManager.getLogger(TelegramBot.class.getName());

    private final String token;
    private final String codeWord;

    Db db;

    TelegramBot(String token, String codeWord, Db db) {

        this.token = token;
        this.codeWord = codeWord;
        this.db = db;

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            logger.error(e);
        }
    }

    public void sendToAll(String message) {
        for(Long chatId : db.getAllUsers()) {
            SendMessage outputMessage = new SendMessage();
            outputMessage.setChatId(chatId.toString());
            outputMessage.setText(message);
            try {
                execute(outputMessage);
            } catch (TelegramApiException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            String inputMessage = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (inputMessage.equals(codeWord)) {
                if (db.isAdmin(chatId).isEmpty()) {
                    addUser(chatId);
                } else {
                    userAlreadyAdded(chatId);
                }
            }
        }
    }

    private void addUser(Long chatId) {
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId.toString());

        boolean isUserAdded = db.insertUser(chatId);
            if (isUserAdded) {
                outputMessage.setText("user added!");
            } else {
                outputMessage.setText("Error! User wasn't added");
            }
        try {
            execute(outputMessage);
        } catch (TelegramApiException e) {
            logger.error(e);
        }
    }

    private void userAlreadyAdded(Long chatId) {
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId.toString());
        outputMessage.setText("this user has already been added!");
        try {
            execute(outputMessage);
        } catch (TelegramApiException e) {
            logger.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "Малыгино";
    }



    @Override
    public String getBotToken() {
        // TODO
        return this.token;
    }
}
