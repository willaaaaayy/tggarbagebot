package com.tgbotmap.service;

import com.tgbotmap.client.N8nClient;
import com.tgbotmap.entity.GarbageLocation;
import com.tgbotmap.exception.GeocodingException;
import com.tgbotmap.integration.TelegramApiClient;
import com.tgbotmap.model.telegram.SendMessageRequest;
import com.tgbotmap.repository.GarbageLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private N8nClient n8nClient;
    @Mock
    private GarbageLocationRepository repository;
    @Mock
    private MapLinkService mapLinkService;
    @Mock
    private TelegramApiClient telegramApiClient;

    @InjectMocks
    private AddressService addressService;

    @Test
    void processMessage_geocodesSavesAndReplies() {
        when(n8nClient.geocode("ул. Ленина 1"))
                .thenReturn(Mono.just(new N8nClient.GeocodeResponse(55.5, 37.5)));
        when(repository.save(any(GarbageLocation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapLinkService.generateLink(anyList()))
                .thenReturn(Optional.of("https://yandex.ru/maps/?pt=37.5,55.5&z=15"));

        addressService.processMessage("  ул. Ленина 1  ", 123L);

        ArgumentCaptor<GarbageLocation> saved = ArgumentCaptor.forClass(GarbageLocation.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getAddress()).isEqualTo("ул. Ленина 1");
        assertThat(saved.getValue().getLatitude()).isEqualTo(55.5);
        assertThat(saved.getValue().getLongitude()).isEqualTo(37.5);

        ArgumentCaptor<SendMessageRequest> reply = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(telegramApiClient).sendMessage(reply.capture());
        assertThat(reply.getValue().getChatId()).isEqualTo(123L);
        assertThat(reply.getValue().getText()).contains("Место добавлено").contains("Открыть карту");
        assertThat(reply.getValue().getParseMode()).isEqualTo("Markdown");
    }

    @Test
    void processMessage_geocodingFails_repliesWithErrorAndDoesNotSave() {
        when(n8nClient.geocode(anyString()))
                .thenReturn(Mono.error(new GeocodingException("boom")));

        addressService.processMessage("nowhere", 99L);

        verify(repository, never()).save(any());
        verify(telegramApiClient).sendMessage(eq(99L), anyString());
    }

    @Test
    void processMessage_blankText_isIgnored() {
        addressService.processMessage("   ", 1L);

        verify(n8nClient, never()).geocode(anyString());
        verify(repository, never()).save(any());
    }
}
