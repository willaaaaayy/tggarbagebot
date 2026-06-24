package com.tgbotmap.service;

import com.tgbotmap.entity.GarbageLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MapLinkServiceTest {

    private final MapLinkService service = new MapLinkService();

    private GarbageLocation loc(double lat, double lon) {
        return GarbageLocation.builder().address("addr").latitude(lat).longitude(lon).build();
    }

    @Test
    void emptyList_returnsEmpty() {
        assertThat(service.generateLink(List.of())).isEmpty();
        assertThat(service.generateLink(null)).isEmpty();
    }

    @Test
    void singlePoint_buildsPointUrl_lonThenLat() {
        Optional<String> link = service.generateLink(List.of(loc(55.751244, 37.618423)));
        assertThat(link).isPresent();
        // pt=lon,lat per Yandex Maps API
        assertThat(link.get()).isEqualTo("https://yandex.ru/maps/?pt=37.618423,55.751244&z=15");
    }

    @Test
    void multiplePoints_buildsRouteUrl_latThenLon() {
        Optional<String> link = service.generateLink(List.of(loc(55.0, 37.0), loc(56.0, 38.0)));
        assertThat(link).isPresent();
        assertThat(link.get()).isEqualTo("https://yandex.ru/maps/?rtext=55.000000,37.000000~56.000000,38.000000&rtt=auto");
    }
}
