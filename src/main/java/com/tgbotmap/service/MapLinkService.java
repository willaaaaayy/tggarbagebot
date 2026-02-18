package com.tgbotmap.service;

import com.tgbotmap.entity.GarbageLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Generates Yandex Maps links from a list of {@link GarbageLocation} entries.
 */
@Slf4j
@Service
public class MapLinkService {

    private static final String YANDEX_MAPS_BASE = "https://yandex.ru/maps/";
    private static final int DEFAULT_ZOOM = 15;

    /**
     * Generates a Yandex Maps link for the given locations.
     *
     * <ul>
     *   <li>Single point: {@code https://yandex.ru/maps/?pt=lon,lat&z=15}</li>
     *   <li>Multiple points (route): {@code https://yandex.ru/maps/?rtext=lat1,lon1~lat2,lon2&rtt=auto}</li>
     *   <li>Empty/null list: returns {@link Optional#empty()}</li>
     * </ul>
     *
     * @param locations list of garbage locations to include in the link
     * @return an {@link Optional} containing the generated URL, or empty if no locations provided
     */
    public Optional<String> generateLink(List<GarbageLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            log.debug("No locations provided, returning empty link");
            return Optional.empty();
        }

        String url;
        if (locations.size() == 1) {
            url = buildSinglePointUrl(locations.getFirst());
        } else {
            url = buildRouteUrl(locations);
        }

        log.debug("Generated Yandex Maps link for {} point(s): {}", locations.size(), url);
        return Optional.of(url);
    }

    private String buildSinglePointUrl(GarbageLocation location) {
        return String.format(Locale.US, "%s?pt=%.6f,%.6f&z=%d",
                YANDEX_MAPS_BASE,
                location.getLongitude(),
                location.getLatitude(),
                DEFAULT_ZOOM);
    }

    private String buildRouteUrl(List<GarbageLocation> locations) {
        String rtext = locations.stream()
                .map(loc -> String.format(Locale.US, "%.6f,%.6f", loc.getLatitude(), loc.getLongitude()))
                .collect(Collectors.joining("~"));

        return String.format("%s?rtext=%s&rtt=auto", YANDEX_MAPS_BASE, rtext);
    }
}
