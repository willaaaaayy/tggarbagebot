package com.tgbotmap.service;

import com.tgbotmap.entity.BotUser;
import com.tgbotmap.model.telegram.Chat;
import com.tgbotmap.model.telegram.TelegramUser;
import com.tgbotmap.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final BotUserRepository botUserRepository;

    /**
     * Registers a new user or returns the existing one.
     * Idempotent operation — safe to call on every incoming message.
     */
    @Transactional
    public BotUser registerOrGet(Chat chat, TelegramUser from) {
        return botUserRepository.findByChatId(chat.getId())
                .map(existing -> {
                    boolean updated = false;
                    if (from != null) {
                        if (from.getUsername() != null && !from.getUsername().equals(existing.getUsername())) {
                            existing.setUsername(from.getUsername());
                            updated = true;
                        }
                        if (from.getFirstName() != null && !from.getFirstName().equals(existing.getFirstName())) {
                            existing.setFirstName(from.getFirstName());
                            updated = true;
                        }
                        if (from.getLastName() != null && !from.getLastName().equals(existing.getLastName())) {
                            existing.setLastName(from.getLastName());
                            updated = true;
                        }
                        if (from.getLanguageCode() != null && !from.getLanguageCode().equals(existing.getLanguageCode())) {
                            existing.setLanguageCode(from.getLanguageCode());
                            updated = true;
                        }
                    }
                    if (updated) {
                        log.info("Updated user info for chatId={}", chat.getId());
                        return botUserRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    BotUser newUser = BotUser.builder()
                            .chatId(chat.getId())
                            .username(from != null ? from.getUsername() : null)
                            .firstName(from != null ? from.getFirstName() : chat.getFirstName())
                            .lastName(from != null ? from.getLastName() : chat.getLastName())
                            .languageCode(from != null ? from.getLanguageCode() : null)
                            .build();
                    BotUser saved = botUserRepository.save(newUser);
                    log.info("Registered new user: chatId={}, username={}", saved.getChatId(), saved.getUsername());
                    return saved;
                });
    }
}
