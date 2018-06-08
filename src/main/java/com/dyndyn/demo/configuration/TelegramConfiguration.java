package com.dyndyn.demo.configuration;

import com.dyndyn.demo.bot.MyAmazingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

/**
 * Created by roman on 03.06.18.
 */
@Configuration
public class TelegramConfiguration {

    @Autowired
    private MyAmazingBot myAmazingBot;

    static {
        ApiContextInitializer.init();

    }

    @PostConstruct
    public void start(){
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(myAmazingBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
