package com.tgbotmap.service;

import com.tgbotmap.integration.TelegramApiClient;
import com.tgbotmap.model.telegram.Message;
import com.tgbotmap.model.telegram.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final UserService userService;
    private final TelegramApiClient telegramApiClient;

    /**
     * Main entry point for processing incoming Telegram updates.
     */
    public void processUpdate(Update update) {
        if (update.getMessage() != null) {
            handleMessage(update.getMessage());
        } else if (update.getCallbackQuery() != null) {
            log.debug("Received callback query: {}", update.getCallbackQuery().getData());
            // Extensible: handle callback queries here
        } else {
            log.debug("Received unsupported update type: {}", update.getUpdateId());
        }
    }

    private void handleMessage(Message message) {
        if (message.getText() == null || message.getChat() == null) {
            return;
        }

        Long chatId = message.getChat().getId();
        String text = message.getText().trim();

        log.info("Received message from chatId={}: {}", chatId, text);

        if (text.startsWith("/")) {
            handleCommand(chatId, text, message);
        } else {
            handleTextMessage(chatId, text, message);
        }
    }

    private void handleCommand(Long chatId, String text, Message message) {
        String command = text.split("\\s+")[0].toLowerCase();

        switch (command) {
            case "/start" -> handleStartCommand(chatId, message);
            case "/help" -> handleHelpCommand(chatId);
            default -> telegramApiClient.sendMessage(chatId,
                    "Unknown command. Use /help to see available commands.");
        }
    }

    private void handleStartCommand(Long chatId, Message message) {
        userService.registerOrGet(message.getChat(), message.getFrom());
        telegramApiClient.sendMessage(chatId,
                "Welcome to TgBotMap! 🗺\n\nUse /help to see available commands.");
    }

    private void handleHelpCommand(Long chatId) {
        String helpText = """
                Available commands:
                /start — Register and start the bot
                /help — Show this help message
                """;
        telegramApiClient.sendMessage(chatId, helpText);
    }

    private void handleTextMessage(Long chatId, String text, Message message) {
        telegramApiClient.sendMessage(chatId,
                "I received your message. Use /help to see available commands.");
    }
}
