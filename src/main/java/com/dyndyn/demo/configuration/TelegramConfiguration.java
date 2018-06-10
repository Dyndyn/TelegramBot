package com.dyndyn.demo.configuration;

import com.dyndyn.demo.bot.MyAmazingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

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
    public void start() throws TelegramApiRequestException {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(myAmazingBot);
    }

}
