package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

public class TelegramBot extends TelegramLongPollingBot {

    @Lazy
    @Autowired
    CheckingLastDate checkingLastDate;

    static Logger logger = LogManager.getLogger(TelegramBot.class.getName());

    private static final String INPUT_DELETE_ME = "deleteMe";
    private static final String INPUT_SET_PING_REGEX = "ping\\s*=\\s*\\d{1,2}";
    private static final String INPUT_SET_PING_REMOVE_PART_REGEX = "ping\\s*=\\s*";

    private final String codeWord;
    private final String adminCodeWord;
    private final Db db;

    TelegramBot(String botToken, String codeWord, String adminCodeWord, Db db) {
        super(botToken);

        this.codeWord = codeWord;
        this.adminCodeWord = adminCodeWord;
        this.db = db;

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            logger.error(e);
        }
    }

    public void sendToAll(String message, boolean onlyAdmins) {
        for(Long chatId : db.getChatIds(onlyAdmins)) {
            sendToTelegram(message, chatId);
        }
    }

    public void sendToTelegram(String message, Long chatId) {
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId.toString());
        outputMessage.setText(message);

        try {
            execute(outputMessage);
        } catch (TelegramApiException e) {
            logger.error("Can't send to telegram: " +
                    "\"" + message + "\", chatId: " +
                    chatId, e);
            return;
        }

        logger.info("Sent to telegram: " +
                "\"" + message + "\", chatId: " +
                chatId);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String inputMessage = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            logger.info("Input telegram message: " + "\""
                    + inputMessage +
                    "\", chatId: " + chatId);

            if (codeWord.equals(inputMessage)) {
                processCodeWord(chatId);
            } else if (adminCodeWord.equals(inputMessage)) {
                processAdminCodeWord(chatId);
            } else if (INPUT_DELETE_ME.equals(inputMessage)) {
                processDeleteMe(chatId);
            } else if (inputMessage.matches(INPUT_SET_PING_REGEX)) {
                processPing(inputMessage, chatId);


            }
        }
    }

    private void processCodeWord(Long chatId) {
        Optional<Boolean> isAdmin;
        try {
            isAdmin = db.isAdmin(chatId);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Пользователь не добавлен", chatId);
            return;
        }

        if (isAdmin.isEmpty()) {
            addRegularUser(chatId);
        } else if (isAdmin.get()) {
            updateAdminToRegularUser(chatId);
        } else {
            sendToTelegram("Этот пользователь уже был добавлен!", chatId);
        }
    }

    private void addRegularUser(Long chatId) {
        int numberUsersAdded;
        try {
            numberUsersAdded = db.insertUser(chatId, false);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Пользователь не добавлен", chatId);
            return;
        }

        if ( 0 == numberUsersAdded) {
            sendToTelegram("Ошибка! 0 пользователей добавлен в бд", chatId);
        } else if (1 == numberUsersAdded) {
            sendToTelegram("Пользователь добавлен!", chatId);
        } else {
            sendToTelegram( "Ошибка! " +  numberUsersAdded + " пользователей добавлено в бд", chatId);
        }
    }

    private void updateAdminToRegularUser(Long chatId) {
        int numberUpdatedUsers;
        try {
            numberUpdatedUsers = db.updatePrivileges(chatId, false);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Права не были изменены", chatId);
            return;
        }

        if (0 == numberUpdatedUsers) {
            sendToTelegram("Ошибка! 0 строк изменено в бд", chatId);
        } else if (1 == numberUpdatedUsers) {
            sendToTelegram("Права изменены на обычного пользователя", chatId);
        } else {
            sendToTelegram("Ошибка! " + numberUpdatedUsers + " строк изменено в бд", chatId);
        }
    }

    private void processAdminCodeWord(Long chatId) {
        Optional<Boolean> isAdmin;
        try {
            isAdmin = db.isAdmin(chatId);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Пользователь не добавлен", chatId);
            return;
        }
        if (isAdmin.isEmpty()) {
            addAdmin(chatId);
        } else if (isAdmin.get()) {
            sendToTelegram("Этот админ уже был добавлен!", chatId);
        } else {
            updateRegularUserToAdmin(chatId);
        }
    }

    private void addAdmin(Long chatId) {
        int numberAdminAdded;
        try {
            numberAdminAdded = db.insertUser(chatId, true);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Админ не был добавлен!", chatId);
            return;
        }

        if ( 0 == numberAdminAdded) {
            sendToTelegram("Ошибка! 0 админов добавлено!", chatId);
        }
        if (1 == numberAdminAdded) {
            sendToTelegram("Админ добавлен!", chatId);
        } else {
            sendToTelegram( "Ошибка! " +  numberAdminAdded + " админов добавлено", chatId);
        }
    }

    private void updateRegularUserToAdmin(Long chatId) {
        int numberUpdatedUsers;
        try {
            numberUpdatedUsers = db.updatePrivileges(chatId, true);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Права не были изменены", chatId);
            return;
        }

        if (0 == numberUpdatedUsers) {
            sendToTelegram("Ошибка! 0 строк изменено в бд", chatId);
        } else if (1 == numberUpdatedUsers) {
            sendToTelegram("Права изменены на админа", chatId);
        } else {
            sendToTelegram("Ошибка! " + numberUpdatedUsers + " строк изменено в бд", chatId);
        }
    }

    private void processDeleteMe(Long chatId) {

        int numberDeletedUsers;
        try {
            numberDeletedUsers = db.deleteUser(chatId);
        } catch (BusinessLogicException e) {
            logger.error(e);
            sendToTelegram("Ошибка! Пользователь/админ не удалён", chatId);
            return;
        }

        if (0 == numberDeletedUsers) {
            sendToTelegram("Ошибка! Такого пользователя/админа нет", chatId);
        } else if (1 == numberDeletedUsers) {
            sendToTelegram("Пользователь/админ удалён!", chatId);
        } else {
            sendToTelegram("Ошибка! Количество удалённых пользователей/админов: " + numberDeletedUsers, chatId);
        }
    }

    private void processPing(String inputMessage, Long chatId) {
        String periodPingStr = inputMessage.replaceFirst(INPUT_SET_PING_REMOVE_PART_REGEX, "");

        int periodPing;
        try {
            periodPing = Integer.parseInt(periodPingStr);
        } catch (NumberFormatException e) {
            sendToTelegram("incorrect format of number - seconds period ping", chatId);
            return;
        }

        checkingLastDate.setPeriodPing(periodPing);

        sendToTelegram("Period ping has been set: " + checkingLastDate.getPeriodPing(), chatId);
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "Малыгино";
    }
}
