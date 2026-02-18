package com.tgbotmap.repository;

import com.tgbotmap.entity.GarbageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface GarbageLocationRepository extends JpaRepository<GarbageLocation, UUID> {

    @Modifying
    @Transactional
    void deleteAllByCreatedAtBefore(LocalDateTime time);
}
