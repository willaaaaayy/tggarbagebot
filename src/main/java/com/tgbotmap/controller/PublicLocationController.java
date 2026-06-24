package com.tgbotmap.controller;

import com.tgbotmap.dto.admin.GarbageLocationDto;
import com.tgbotmap.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public, read-only access to garbage locations for the map page.
 * Exposed under {@code /public/**} (permitted without authentication) so the map can render
 * points without embedding admin credentials in client-side code.
 */
@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicLocationController {

    private final AdminService adminService;

    @GetMapping("/locations")
    public ResponseEntity<List<GarbageLocationDto>> getLocations() {
        List<GarbageLocationDto> locations = adminService.getAllLocations();
        log.debug("GET /public/locations - returning {} locations", locations.size());
        return ResponseEntity.ok(locations);
    }
}
