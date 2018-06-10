package com.dyndyn.demo.bot;

import com.dyndyn.demo.service.PlacesService;
import com.google.maps.model.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyAmazingBot extends TelegramLongPollingBot {
    private static final int MAX_MESSAGE_LENGTH = 4096;
    private static final Logger logger = LoggerFactory.getLogger(TelegramLongPollingBot.class);

    @Resource
    private Environment environment;

    private Map<Long, Integer> lastMessageIDs = new HashMap<>();
    private List<String> places = new ArrayList<>();
    private PlacesService placesService;

    @Autowired
    public MyAmazingBot(PlacesService placesService) {
        super();
        this.placesService = placesService;
        places.add("Location");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            sendPlaces(update);
        } else if (update.getMessage().hasLocation()) {
            sendTypes(update);
        } else {
            SendMessage message = new SendMessage() // Create a message object object
                    .setText("Please pass your location")
                    .setChatId(update.getMessage().getChatId())
                    .setReplyMarkup(getReplyKeyboardMarkup(places));

            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                logger.error(e.toString());
            }
        }

    }

    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @bot.MyAmazingBot, it must return 'bot.MyAmazingBot'
        return environment.getProperty("telegram.bot.name");
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return environment.getProperty("telegram.token");
    }

    private void sendTypes(Update update){
        Long chatId = update.getMessage().getChatId();
        LatLng latLng = new LatLng(update.getMessage().getLocation().getLatitude(),
                update.getMessage().getLocation().getLongitude());

        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(update.getMessage().getChatId()).setText("Choose Type")
                .setReplyMarkup(getInlineKeyboardMarkup(placesService.getTypes(latLng, chatId)));

        lastMessageIDs.remove(chatId);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            logger.error(e.toString());
        }
    }

    private void sendPlaces(Update update){
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String results = placesService.getPlaces(update.getCallbackQuery().getData(), chatId);

        try {
            if (lastMessageIDs.get(chatId) != null){
                deleteMessage(new DeleteMessage(chatId, lastMessageIDs.get(chatId)));
            }
            while (results.length() > MAX_MESSAGE_LENGTH){
                int index = results.substring(0, MAX_MESSAGE_LENGTH).lastIndexOf("\n\n");
                sendTextMessage(chatId, results.substring(0, index));
                results = results.substring(index + 1);
            }

            Integer messageId = sendTextMessage(chatId, results);
            lastMessageIDs.put(chatId, messageId); // Sending our message object to user
        } catch (TelegramApiException e) {
            logger.error(e.toString());
            lastMessageIDs.remove(chatId);
        }
    }

    private Integer sendTextMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText(text);
        return execute(message).getMessageId();
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup(List<String> buttons) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowsInline = new ArrayList<>();

        int columnCounter = 1;
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < buttons.size(); i++) {
            KeyboardButton button = new KeyboardButton().setRequestLocation(true).setText(buttons.get(i));
            row.add(button);

            if (columnCounter == 2 || i == buttons.size() - 1) {
                rowsInline.add(row);
            }
            if (columnCounter < 2) {
                columnCounter++;
            } else {
                columnCounter = 1;
                row = new KeyboardRow();
            }
        }

        replyKeyboardMarkup.setKeyboard(rowsInline);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<String> buttons) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        int columnCounter = 1;
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {

            rowInline.add(new InlineKeyboardButton().setText(buttons.get(i)).setCallbackData(buttons.get(i)));

            if (columnCounter == 2 || i == buttons.size() - 1) {
                rowsInline.add(rowInline);
            }
            if (columnCounter < 2) {
                columnCounter++;
            } else {
                columnCounter = 1;
                rowInline = new ArrayList<>();
            }
        }

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}