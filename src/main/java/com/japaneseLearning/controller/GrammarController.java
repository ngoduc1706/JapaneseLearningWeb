package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.entity.Grammar;
import com.japaneseLearning.repository.GrammarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grammars")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GrammarController {
    
    private final GrammarRepository grammarRepository;
    private final com.japaneseLearning.service.EnrollmentService enrollmentService;
    private final com.japaneseLearning.repository.LessonRepository lessonRepository;

    public GrammarController(GrammarRepository grammarRepository,
                           com.japaneseLearning.service.EnrollmentService enrollmentService,
                           com.japaneseLearning.repository.LessonRepository lessonRepository) {
        this.grammarRepository = grammarRepository;
        this.enrollmentService = enrollmentService;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Grammar>>> getAllGrammars() {
        List<Grammar> grammars = grammarRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(grammars, "Grammars retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Grammar>> getGrammarById(@PathVariable Long id) {
        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success(grammar, "Grammar retrieved successfully"));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<Grammar>>> getGrammarsByLesson(@PathVariable Long lessonId) {
        // SECURITY CHECK
        if (!isUserAuthorizedForLesson(lessonId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }
        
        List<Grammar> grammars = grammarRepository.findByLesson_LessonId(lessonId);
        return ResponseEntity.ok(ApiResponse.success(grammars, "Lesson grammars retrieved successfully"));
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

    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<Grammar>>> getGrammarsByLevel(@PathVariable String level) {
        List<Grammar> grammars = grammarRepository.findByLevel(level);
        return ResponseEntity.ok(ApiResponse.success(grammars, "Grammars by level retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Grammar>> createGrammar(@RequestBody Grammar grammar) {
        Grammar saved = grammarRepository.save(grammar);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Grammar created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Grammar>> updateGrammar(@PathVariable Long id, @RequestBody Grammar grammarDetails) {
        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar not found with id: " + id));
        
        grammar.setRule(grammarDetails.getRule());
        grammar.setExample(grammarDetails.getExample());
        grammar.setLevel(grammarDetails.getLevel());
        grammar.setNotes(grammarDetails.getNotes());
        
        Grammar updated = grammarRepository.save(grammar);
        return ResponseEntity.ok(ApiResponse.success(updated, "Grammar updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGrammar(@PathVariable Long id) {
        grammarRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Grammar deleted successfully"));
    }
}
