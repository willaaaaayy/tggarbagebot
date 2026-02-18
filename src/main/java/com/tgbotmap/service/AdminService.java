package com.tgbotmap.service;

import com.tgbotmap.dto.admin.GarbageLocationDto;
import com.tgbotmap.entity.GarbageLocation;
import com.tgbotmap.repository.GarbageLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final GarbageLocationRepository garbageLocationRepository;
    private final MapLinkService mapLinkService;

    public List<GarbageLocationDto> getAllLocations() {
        log.debug("Fetching all garbage locations");
        return garbageLocationRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<String> getMapLink() {
        log.debug("Generating map link for all locations");
        List<GarbageLocation> locations = garbageLocationRepository.findAll();
        return mapLinkService.generateLink(locations);
    }

    public boolean deleteLocation(UUID id) {
        log.info("Deleting garbage location with id={}", id);
        if (garbageLocationRepository.existsById(id)) {
            garbageLocationRepository.deleteById(id);
            log.info("Successfully deleted garbage location id={}", id);
            return true;
        }
        log.warn("Garbage location not found for deletion: id={}", id);
        return false;
    }

    private GarbageLocationDto toDto(GarbageLocation entity) {
        return GarbageLocationDto.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
