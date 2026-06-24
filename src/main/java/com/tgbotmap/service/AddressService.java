package com.tgbotmap.service;

import com.tgbotmap.client.N8nClient;
import com.tgbotmap.entity.GarbageLocation;
import com.tgbotmap.exception.GeocodingException;
import com.tgbotmap.integration.TelegramApiClient;
import com.tgbotmap.model.telegram.SendMessageRequest;
import com.tgbotmap.repository.GarbageLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Core pipeline for turning a plain-text address message from the Telegram group
 * into a persisted {@link GarbageLocation}:
 * <pre>
 *   text → N8nClient.geocode → save GarbageLocation → reply with a map link
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final N8nClient n8nClient;
    private final GarbageLocationRepository garbageLocationRepository;
    private final MapLinkService mapLinkService;
    private final TelegramApiClient telegramApiClient;

    /**
     * Processes an incoming text message from the allowed Telegram group chat:
     * geocodes the address, persists it, and replies with a Yandex Maps link.
     *
     * @param text   the raw text message from Telegram (treated as an address)
     * @param chatId the chat to reply to
     */
    public void processMessage(String text, Long chatId) {
        String address = text == null ? "" : text.trim();
        if (address.isBlank()) {
            log.debug("Empty address message from chatId={}, ignoring", chatId);
            return;
        }

        log.info("Processing address message from chatId={}: {}", chatId, address);

        N8nClient.GeocodeResponse geo;
        try {
            geo = n8nClient.geocode(address).block();
        } catch (GeocodingException e) {
            log.warn("Geocoding failed for address '{}': {}", address, e.getMessage());
            telegramApiClient.sendMessage(chatId,
                    "⚠️ Не удалось определить координаты для адреса: " + address);
            return;
        }

        if (geo == null) {
            log.warn("Geocoding returned no result for address '{}'", address);
            telegramApiClient.sendMessage(chatId,
                    "⚠️ Не удалось определить координаты для адреса: " + address);
            return;
        }

        GarbageLocation location = GarbageLocation.builder()
                .address(address)
                .latitude(geo.lat())
                .longitude(geo.lon())
                .build();
        garbageLocationRepository.save(location);
        log.info("Saved garbage location id={} for address='{}' ({}, {})",
                location.getId(), address, geo.lat(), geo.lon());

        String reply = "📍 Место добавлено: " + address;
        String mapLink = mapLinkService.generateLink(List.of(location)).orElse(null);
        if (mapLink != null) {
            reply += "\n🗺 [Открыть карту](" + mapLink + ")";
        }

        telegramApiClient.sendMessage(SendMessageRequest.builder()
                .chatId(chatId)
                .text(reply)
                .parseMode("Markdown")
                .build());
    }
}
