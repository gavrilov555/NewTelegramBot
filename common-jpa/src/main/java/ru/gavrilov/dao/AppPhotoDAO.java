package ru.gavrilov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilov.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
