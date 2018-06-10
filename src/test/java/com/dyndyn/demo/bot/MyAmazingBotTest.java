package com.dyndyn.demo.bot;

import com.dyndyn.demo.service.PlacesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The MyAmazingBotTest class is used to test MyAmazingBot class methods
 *
 * @author Roman Dyndyn
 */
@RunWith(MockitoJUnitRunner.class)
public class MyAmazingBotTest {

    @Mock
    private Update update;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private Message message;
    @Mock
    private PlacesService placesService;

    @InjectMocks
    @Spy
    private MyAmazingBot myAmazingBot;

    @Test
    public void testOnUpdateReceived_shouldSendPlaces() throws TelegramApiException {

        String results = "test";
        Long chatId = 1L;
        Message returnMessage = mock(Message.class);
        when(placesService.getPlaces(update))
                .thenReturn(results);
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        doReturn(returnMessage).when(myAmazingBot).execute(any());

        myAmazingBot.onUpdateReceived(update);

        verify(placesService, times(1)).getPlaces(update);
        verify(myAmazingBot, times(1)).execute(any());
    }
}
