package com.tgbotmap.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddressService {

    /**
     * Processes an incoming text message from the allowed Telegram group chat.
     * This is a stub — implement actual address parsing/geocoding logic here.
     *
     * @param text the raw text message from Telegram
     */
    public void processMessage(String text) {
        log.info("Processing address message: {}", text);
        // TODO: implement address extraction, geocoding, persistence, etc.
    }
}
