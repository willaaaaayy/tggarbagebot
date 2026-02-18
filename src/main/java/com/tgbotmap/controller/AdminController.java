package com.tgbotmap.controller;

import com.tgbotmap.dto.admin.GarbageLocationDto;
import com.tgbotmap.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/locations")
    public ResponseEntity<List<GarbageLocationDto>> getAllLocations() {
        log.info("GET /admin/locations - fetching all locations");
        List<GarbageLocationDto> locations = adminService.getAllLocations();
        log.debug("Returning {} locations", locations.size());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/map")
    public ResponseEntity<String> getMapLink() {
        log.info("GET /admin/map - generating map link");
        return adminService.getMapLink()
                .map(link -> {
                    log.debug("Generated map link: {}", link);
                    return ResponseEntity.ok(link);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        log.info("DELETE /admin/locations/{} - deleting location", id);
        boolean deleted = adminService.deleteLocation(id);
        if (deleted) {
            log.info("Location {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("Location {} not found for deletion", id);
        return ResponseEntity.notFound().build();
    }
}
