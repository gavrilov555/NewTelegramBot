package ru.gavrilov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilov.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long>{
}
