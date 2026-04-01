package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.entity.AsrWeeklyRecord;
import com.japaneseLearning.service.WeeklyAsrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asr")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AsrController {
    
    private final WeeklyAsrService weeklyAsrService;

    public AsrController(WeeklyAsrService weeklyAsrService) {
        this.weeklyAsrService = weeklyAsrService;
    }

    @PostMapping("/practice/{userId}")
    public ResponseEntity<ApiResponse<Void>> recordPractice(@PathVariable String userId) {
        weeklyAsrService.increaseAsync(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Practice recorded successfully"));
    }

    @GetMapping("/weekly/{userId}")
    public ResponseEntity<ApiResponse<Optional<AsrWeeklyRecord>>> getCurrentWeekly(@PathVariable String userId) {
        Optional<AsrWeeklyRecord> record = weeklyAsrService.getCurrentAsync(userId);
        return ResponseEntity.ok(ApiResponse.success(record, "Current weekly record retrieved"));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<AsrWeeklyRecord>>> getWeeklyHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "6") Integer weeks) {
        List<AsrWeeklyRecord> records = weeklyAsrService.getLastWeeksAsync(userId, weeks);
        return ResponseEntity.ok(ApiResponse.success(records, "Weekly history retrieved"));
    }
}
