package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.dto.SrsReviewDTO;
import com.japaneseLearning.dto.VocabularyDTO;
import com.japaneseLearning.dto.VocabularyProgressDTO;
import com.japaneseLearning.entity.Vocabulary;
import com.japaneseLearning.repository.VocabularyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vocabularies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VocabularyController {
    
    private final VocabularyRepository vocabularyRepository;
    private final com.japaneseLearning.service.EnrollmentService enrollmentService;
    private final com.japaneseLearning.repository.LessonRepository lessonRepository;
    private final com.japaneseLearning.service.VocabularyProgressService vocabularyProgressService;

    public VocabularyController(VocabularyRepository vocabularyRepository, 
                               com.japaneseLearning.service.EnrollmentService enrollmentService,
                               com.japaneseLearning.repository.LessonRepository lessonRepository,
                               com.japaneseLearning.service.VocabularyProgressService vocabularyProgressService) {
        this.vocabularyRepository = vocabularyRepository;
        this.enrollmentService = enrollmentService;
        this.lessonRepository = lessonRepository;
        this.vocabularyProgressService = vocabularyProgressService;
    }

    /**
     * JL-39: Bookmark vocabulary as favorite.
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<VocabularyProgressDTO>> addFavorite(@PathVariable Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
        Long lessonId = vocabulary.getLesson() != null ? vocabulary.getLesson().getLessonId() : null;

        if (lessonId != null && !isUserAuthorizedForLesson(lessonId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }

        VocabularyProgressDTO dto = vocabularyProgressService.setFavorite(getCurrentUserId(), id, true);
        return ResponseEntity.ok(ApiResponse.success(dto, "Vocabulary bookmarked successfully"));
    }

    /**
     * JL-39: Remove bookmark from vocabulary.
     */
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<VocabularyProgressDTO>> removeFavorite(@PathVariable Long id) {
        VocabularyProgressDTO dto = vocabularyProgressService.setFavorite(getCurrentUserId(), id, false);
        return ResponseEntity.ok(ApiResponse.success(dto, "Vocabulary bookmark removed"));
    }

    /**
     * JL-39: Get current user's favorite vocabularies.
     */
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<VocabularyProgressDTO>>> getFavorites() {
        List<VocabularyProgressDTO> favorites = vocabularyProgressService.getFavorites(getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(favorites, "Favorite vocabularies retrieved successfully"));
    }

    /**
     * JL-40: Update SRS after user reviews a vocabulary item.
     */
    @PostMapping("/{id}/srs/review")
    public ResponseEntity<ApiResponse<VocabularyProgressDTO>> reviewSrs(
            @PathVariable Long id,
            @RequestBody SrsReviewDTO reviewDTO) {
        int quality = reviewDTO != null && reviewDTO.quality() != null ? reviewDTO.quality() : 0;
        VocabularyProgressDTO updated = vocabularyProgressService.reviewSrs(getCurrentUserId(), id, quality);
        return ResponseEntity.ok(ApiResponse.success(updated, "SRS progress updated successfully"));
    }

    /**
     * JL-40: Get vocabularies due for review in SRS.
     */
    @GetMapping("/srs/due")
    public ResponseEntity<ApiResponse<List<VocabularyProgressDTO>>> getDueSrsReviews() {
        List<VocabularyProgressDTO> dueList = vocabularyProgressService.getDueReviews(getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(dueList, "Due SRS reviews retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VocabularyDTO>>> getAllVocabularies() {
        List<VocabularyDTO> vocabularies = vocabularyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(vocabularies, "Vocabularies retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VocabularyDTO>> getVocabularyById(@PathVariable Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(vocabulary), "Vocabulary retrieved successfully"));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<VocabularyDTO>>> getVocabulariesByLesson(@PathVariable Long lessonId) {
        // SECURITY CHECK
        if (!isUserAuthorizedForLesson(lessonId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }
        
        List<VocabularyDTO> vocabularies = vocabularyRepository.findByLesson_LessonId(lessonId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(vocabularies, "Lesson vocabularies retrieved successfully"));
    }

    private boolean isUserAuthorizedForLesson(Long lessonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            throw new RuntimeException("User not authenticated");
        }
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VocabularyDTO>> createVocabulary(@RequestBody VocabularyDTO vocabularyDTO) {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setKanji(vocabularyDTO.getKanji());
        vocabulary.setHiragana(vocabularyDTO.getHiragana());
        vocabulary.setRomaji(vocabularyDTO.getRomaji());
        vocabulary.setMeaning(vocabularyDTO.getMeaning());
        
        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(convertToDTO(saved), "Vocabulary created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VocabularyDTO>> updateVocabulary(@PathVariable Long id, @RequestBody VocabularyDTO vocabularyDTO) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
        
        vocabulary.setKanji(vocabularyDTO.getKanji());
        vocabulary.setHiragana(vocabularyDTO.getHiragana());
        vocabulary.setRomaji(vocabularyDTO.getRomaji());
        vocabulary.setMeaning(vocabularyDTO.getMeaning());
        
        Vocabulary updated = vocabularyRepository.save(vocabulary);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Vocabulary updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVocabulary(@PathVariable Long id) {
        vocabularyRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vocabulary deleted successfully"));
    }

    private VocabularyDTO convertToDTO(Vocabulary vocabulary) {
        VocabularyDTO dto = new VocabularyDTO();
        dto.setId(vocabulary.getId());
        dto.setKanji(vocabulary.getKanji());
        dto.setHiragana(vocabulary.getHiragana());
        dto.setRomaji(vocabulary.getRomaji());
        dto.setMeaning(vocabulary.getMeaning());
        dto.setLessonId(vocabulary.getLesson() != null ? vocabulary.getLesson().getLessonId() : null);
        return dto;
    }
}
