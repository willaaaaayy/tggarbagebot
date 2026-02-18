package com.tgbotmap.repository;

import com.tgbotmap.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, UUID> {

    Optional<BotUser> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
