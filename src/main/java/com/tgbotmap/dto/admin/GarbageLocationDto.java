package com.tgbotmap.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarbageLocationDto {

    private UUID id;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private Long chatId;
    private String description;
}
