package ru.gavrilov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilov.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
