package com.tgbotmap.service;

import com.tgbotmap.entity.BotUser;
import com.tgbotmap.integration.TelegramApiClient;
import com.tgbotmap.model.telegram.Chat;
import com.tgbotmap.model.telegram.Message;
import com.tgbotmap.model.telegram.Update;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BotServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private TelegramApiClient telegramApiClient;
    @Mock
    private AddressService addressService;

    @InjectMocks
    private BotService botService;

    private Update textUpdate(long chatId, String text) {
        Chat chat = new Chat();
        chat.setId(chatId);
        Message message = new Message();
        message.setChat(chat);
        message.setText(text);
        Update update = new Update();
        update.setMessage(message);
        return update;
    }

    @Test
    void startCommand_registersUserAndSendsWelcome() {
        when(userService.registerOrGet(any(), any())).thenReturn(new BotUser());

        botService.processUpdate(textUpdate(10L, "/start"));

        verify(userService).registerOrGet(any(), any());
        verify(telegramApiClient).sendMessage(eq(10L), contains("Welcome"));
        verifyNoInteractions(addressService);
    }

    @Test
    void plainText_isRoutedToAddressService() {
        botService.processUpdate(textUpdate(20L, "ул. Пушкина 5"));

        verify(addressService).processMessage("ул. Пушкина 5", 20L);
    }

    @Test
    void unknownCommand_repliesWithHint() {
        botService.processUpdate(textUpdate(30L, "/wat"));

        verify(telegramApiClient).sendMessage(eq(30L), contains("Unknown command"));
        verifyNoInteractions(addressService);
    }
}
