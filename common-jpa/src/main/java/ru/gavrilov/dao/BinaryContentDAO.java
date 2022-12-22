package ru.gavrilov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilov.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
