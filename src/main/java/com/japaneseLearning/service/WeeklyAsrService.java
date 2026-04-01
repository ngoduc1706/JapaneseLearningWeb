package com.japaneseLearning.service;

import com.japaneseLearning.entity.AsrWeeklyRecord;
import com.japaneseLearning.repository.AsrWeeklyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WeeklyAsrService {
    
    private final AsrWeeklyRecordRepository asrWeeklyRecordRepository;
    
    public WeeklyAsrService(AsrWeeklyRecordRepository asrWeeklyRecordRepository) {
        this.asrWeeklyRecordRepository = asrWeeklyRecordRepository;
    }

    public void increaseAsync(String userId) {
        LocalDateTime now = LocalDateTime.now();
        Integer week = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        Integer year = now.get(IsoFields.WEEK_BASED_YEAR);

        Optional<AsrWeeklyRecord> record = 
            asrWeeklyRecordRepository.findByUserIdAndYearAndWeek(userId, year, week);

        AsrWeeklyRecord weeklyRecord;
        if (record.isPresent()) {
            weeklyRecord = record.get();
            weeklyRecord.setDoneCount(weeklyRecord.getDoneCount() + 1);
        } else {
            weeklyRecord = new AsrWeeklyRecord();
            weeklyRecord.setUserId(userId);
            weeklyRecord.setYear(year);
            weeklyRecord.setWeek(week);
            weeklyRecord.setDoneCount(1);
            weeklyRecord.setTargetCount(6);
            weeklyRecord.setRewardUnlocked(false);
            weeklyRecord.setCreatedAt(now);
        }

        // Check if reward should be unlocked
        if (weeklyRecord.getDoneCount() >= weeklyRecord.getTargetCount() && 
            !weeklyRecord.getRewardUnlocked()) {
            weeklyRecord.setRewardUnlocked(true);
        }

        asrWeeklyRecordRepository.save(weeklyRecord);
    }

    public List<AsrWeeklyRecord> getLastWeeksAsync(String userId, Integer weekCount) {
        List<AsrWeeklyRecord> records = 
            asrWeeklyRecordRepository.findByUserIdOrderByYearDescWeekDesc(userId);
        return records.stream().limit(weekCount).toList();
    }

    public Optional<AsrWeeklyRecord> getCurrentAsync(String userId) {
        LocalDateTime now = LocalDateTime.now();
        Integer week = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        Integer year = now.get(IsoFields.WEEK_BASED_YEAR);
        
        return asrWeeklyRecordRepository.findByUserIdAndYearAndWeek(userId, year, week);
    }
}
