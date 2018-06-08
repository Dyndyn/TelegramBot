package com.dyndyn.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyAmazingBot extends TelegramLongPollingBot {

    List<String> callbackNames = new ArrayList<>();
    List<String> places = new ArrayList<>();

    public MyAmazingBot() {
        super();
        places.add("Bar");
        places.add("Food");
        places.add("cafe");
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());

        if (update.getMessage().hasLocation()){

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyCPlZw3JX2Uvgc5MZ_zDaewp0YdjRUO6TU")
                    .build();

            LatLng location = new LatLng(update.getMessage().getLocation().getLatitude(),
                    update.getMessage().getLocation().getLongitude());

            PlacesSearchResponse response = null;
            try {
                response = PlacesApi.nearbySearchQuery(context, location).radius(2000).await();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(update.getMessage().getChatId())
                    .setText(Arrays.stream(response.results).map(item -> item.name + " -> " + Arrays.toString(item.types))
                            .collect(Collectors.joining("\n")));

            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else {
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(update.getMessage().getChatId()).setText("test")
                    .setReplyMarkup(getReplyKeyboardMarkup(places));

            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @bot.MyAmazingBot, it must return 'bot.MyAmazingBot'
        return "Dyndyn Demo Bot ";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "615818484:AAHKHnxzq7DBzsM_KaSP_lSsVPxIbGQLdoY";
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup(List<String> buttons) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowsInline = new ArrayList<>();

        int columnCounter = 1;
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < buttons.size(); i++) {
            KeyboardButton button = new KeyboardButton().setRequestLocation(true).setText(buttons.get(i));
            row.add(button);
            callbackNames.add(buttons.get(i));

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
}