package com.tgbotmap.controller;

import com.tgbotmap.config.TelegramBotProperties;
import com.tgbotmap.model.telegram.Chat;
import com.tgbotmap.model.telegram.Message;
import com.tgbotmap.model.telegram.Update;
import com.tgbotmap.service.BotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private BotService botService;
    @Mock
    private TelegramBotProperties botProperties;

    @InjectMocks
    private WebhookController controller;

    private Update update(long chatId) {
        Chat chat = new Chat();
        chat.setId(chatId);
        Message message = new Message();
        message.setChat(chat);
        message.setText("hi");
        Update update = new Update();
        update.setMessage(message);
        return update;
    }

    @Test
    void invalidSecretToken_isRejectedWith403_andNotProcessed() {
        when_secret("expected");

        ResponseEntity<Void> response = controller.onUpdate(update(1L), "wrong");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(botService, never()).processUpdate(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void validSecretAndAllowedChat_isProcessed() {
        when_secret("expected");
        org.mockito.Mockito.when(botProperties.getAllowedGroupId()).thenReturn(42L);

        ResponseEntity<Void> response = controller.onUpdate(update(42L), "expected");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(botService).processUpdate(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void unauthorizedChat_isIgnoredWith200() {
        org.mockito.Mockito.when(botProperties.getWebhookSecret()).thenReturn(null);
        org.mockito.Mockito.when(botProperties.getAllowedGroupId()).thenReturn(42L);

        ResponseEntity<Void> response = controller.onUpdate(update(999L), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(botService, never()).processUpdate(org.mockito.ArgumentMatchers.any());
    }

    private void when_secret(String secret) {
        org.mockito.Mockito.when(botProperties.getWebhookSecret()).thenReturn(secret);
    }
}
