package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.dto.KanjiDTO;
import com.japaneseLearning.entity.Kanji;
import com.japaneseLearning.repository.KanjiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kanjis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class KanjiController {
    
    private final KanjiRepository kanjiRepository;
    private final com.japaneseLearning.service.EnrollmentService enrollmentService;
    private final com.japaneseLearning.repository.LessonRepository lessonRepository;

    public KanjiController(KanjiRepository kanjiRepository,
                         com.japaneseLearning.service.EnrollmentService enrollmentService,
                         com.japaneseLearning.repository.LessonRepository lessonRepository) {
        this.kanjiRepository = kanjiRepository;
        this.enrollmentService = enrollmentService;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KanjiDTO>>> getAllKanjis() {
        List<Kanji> kanjis = kanjiRepository.findAll();
        List<KanjiDTO> kanjiDtos = new ArrayList<>();
        for (Kanji kanji : kanjis) {
            kanjiDtos.add(toDto(kanji));
        }
        return ResponseEntity.ok(ApiResponse.success(kanjiDtos, "Kanjis retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KanjiDTO>> getKanjiById(@PathVariable Long id) {
        Kanji kanji = kanjiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kanji not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success(toDto(kanji), "Kanji retrieved successfully"));
    }

    @GetMapping("/character/{character}")
    public ResponseEntity<ApiResponse<KanjiDTO>> getKanjiByCharacter(@PathVariable String character) {
        Kanji kanji = kanjiRepository.findByCharacter(character);
        if (kanji == null) {
            throw new RuntimeException("Kanji not found with character: " + character);
        }
        return ResponseEntity.ok(ApiResponse.success(toDto(kanji), "Kanji retrieved successfully"));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<KanjiDTO>>> getKanjisByLesson(@PathVariable Long lessonId) {
        // SECURITY CHECK
        if (!isUserAuthorizedForLesson(lessonId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }
        
        List<Kanji> kanjis = kanjiRepository.findByLesson_LessonId(lessonId);
        List<KanjiDTO> kanjiDtos = new ArrayList<>();
        for (Kanji kanji : kanjis) {
            kanjiDtos.add(toDto(kanji));
        }
        return ResponseEntity.ok(ApiResponse.success(kanjiDtos, "Lesson kanjis retrieved successfully"));
    }

    private boolean isUserAuthorizedForLesson(Long lessonId) {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || 
            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            return false;
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        return lessonRepository.findById(lessonId)
            .map(lesson -> enrollmentService.isUserEnrolledInCourse(auth.getName(), lesson.getCourse().getCourseId()))
            .orElse(false);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Kanji>> createKanji(@RequestBody Kanji kanji) {
        Kanji saved = kanjiRepository.save(kanji);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Kanji created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Kanji>> updateKanji(@PathVariable Long id, @RequestBody Kanji kanjiDetails) {
        Kanji kanji = kanjiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kanji not found with id: " + id));
        
        kanji.setCharacter(kanjiDetails.getCharacter());
        kanji.setOnYomi(kanjiDetails.getOnYomi());
        kanji.setKunYomi(kanjiDetails.getKunYomi());
        kanji.setMeaning(kanjiDetails.getMeaning());
        kanji.setHanjaMeaning(kanjiDetails.getHanjaMeaning());
        
        Kanji updated = kanjiRepository.save(kanji);
        return ResponseEntity.ok(ApiResponse.success(updated, "Kanji updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKanji(@PathVariable Long id) {
        kanjiRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Kanji deleted successfully"));
    }

    @GetMapping("/{id}/strokes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKanjiWithStrokes(@PathVariable Long id) {
        Kanji kanji = kanjiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kanji not found with id: " + id));
        
        Map<String, Object> response = Map.of(
            "kanji", kanji,
            "strokeCount", kanji.getStrokes().size(),
            "radicals", kanji.getRadicals().size()
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Kanji with strokes retrieved"));
    }

    private KanjiDTO toDto(Kanji kanji) {
        return new KanjiDTO(
                kanji.getId(),
                kanji.getCharacter(),
                kanji.getOnYomi(),
                kanji.getKunYomi(),
                kanji.getMeaning(),
                kanji.getHanjaMeaning()
        );
    }
}
