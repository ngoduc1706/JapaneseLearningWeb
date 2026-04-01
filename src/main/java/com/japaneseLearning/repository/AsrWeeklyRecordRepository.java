package com.japaneseLearning.repository;

import com.japaneseLearning.entity.AsrWeeklyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsrWeeklyRecordRepository extends JpaRepository<AsrWeeklyRecord, Long> {
    List<AsrWeeklyRecord> findByUserIdOrderByYearDescWeekDesc(String userId);
    Optional<AsrWeeklyRecord> findByUserIdAndYearAndWeek(String userId, Integer year, Integer week);
}
