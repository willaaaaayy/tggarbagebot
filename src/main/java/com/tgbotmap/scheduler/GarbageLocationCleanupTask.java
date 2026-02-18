package com.tgbotmap.scheduler;

import com.tgbotmap.repository.GarbageLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class GarbageLocationCleanupTask {

    private final GarbageLocationRepository garbageLocationRepository;

    /**
     * Removes garbage location records older than 3 hours.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void cleanupOldLocations() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(3);
        log.info("Running garbage location cleanup: removing records created before {}", threshold);

        garbageLocationRepository.deleteAllByCreatedAtBefore(threshold);

        log.info("Garbage location cleanup completed");
    }
}
