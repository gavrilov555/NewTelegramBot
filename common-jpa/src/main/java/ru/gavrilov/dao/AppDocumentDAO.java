package ru.gavrilov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilov.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
