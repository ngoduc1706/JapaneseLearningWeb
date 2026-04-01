package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.dto.SubmitQuizDTO;
import com.japaneseLearning.dto.QuizScoreDTO;
import com.japaneseLearning.dto.WrongAnswerReviewDTO;
import com.japaneseLearning.entity.Option;
import com.japaneseLearning.entity.Quiz;
import com.japaneseLearning.entity.Question;
import com.japaneseLearning.entity.UserQuizAttempt;
import com.japaneseLearning.repository.QuizRepository;
import com.japaneseLearning.repository.UserQuizAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuizController {
    
    private final QuizRepository quizRepository;
    private final UserQuizAttemptRepository attemptRepository;

    public QuizController(QuizRepository quizRepository,
                         UserQuizAttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Quiz>>> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(quizzes, "Quizzes retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success(quiz, "Quiz retrieved successfully"));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Quiz>>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<Quiz> quizzes = quizRepository.findByCourse_CourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(quizzes, "Quizzes by course retrieved successfully"));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<Quiz>>> getQuizzesByLesson(@PathVariable Long lessonId) {
        List<Quiz> quizzes = quizRepository.findByLesson_LessonId(lessonId);
        return ResponseEntity.ok(ApiResponse.success(quizzes, "Quizzes by lesson retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(@RequestBody Quiz quiz) {
        Quiz saved = quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Quiz created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        
        quiz.setTitle(quizDetails.getTitle());
        
        Quiz updated = quizRepository.save(quiz);
        return ResponseEntity.ok(ApiResponse.success(updated, "Quiz updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {
        quizRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Quiz deleted successfully"));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuizDetails(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        
        Map<String, Object> details = Map.of(
            "quiz", quiz,
            "questionCount", quiz.getQuestions().size(),
            "questions", quiz.getQuestions()
        );
        return ResponseEntity.ok(ApiResponse.success(details, "Quiz details retrieved"));
    }

    /**
         * JL-45: Random question algorithm for quiz practice
         */
        @GetMapping("/{quizId}/random")
        public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRandomQuestions(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

            List<Question> randomizedQuestions = new ArrayList<>(quiz.getQuestions());
            if (randomizedQuestions.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No questions found for this quiz"));
            }

            Collections.shuffle(randomizedQuestions);
            int maxSize = Math.min(Math.max(limit, 1), randomizedQuestions.size());

            List<Map<String, Object>> result = randomizedQuestions.stream()
                .limit(maxSize)
                .map(question -> {
                List<Option> randomizedOptions = new ArrayList<>(question.getOptions());
                Collections.shuffle(randomizedOptions);

                List<Map<String, Object>> options = randomizedOptions.stream()
                    .map(option -> Map.<String, Object>of(
                        "optionId", option.getOptionId(),
                        "text", option.getText()
                    ))
                    .collect(Collectors.toList());

                return Map.<String, Object>of(
                    "questionId", question.getQuestionId(),
                    "text", question.getText(),
                    "options", options
                );
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(result, "Random quiz questions generated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error getting random questions: " + e.getMessage()));
        }
        }

        /**
     * JL-42: Submit quiz and calculate score
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuizScoreDTO>> submitQuiz(@RequestBody SubmitQuizDTO submitDTO) {
        try {
            String userId = getCurrentUserId();
            
            Quiz quiz = quizRepository.findById(submitDTO.quizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
            
            List<Question> questions = quiz.getQuestions().stream().toList();
            int correctAnswers = 0;
            
            // Calculate correct answers
            for (int i = 0; i < submitDTO.selectedAnswers().length && i < questions.size(); i++) {
                Question question = questions.get(i);
                Integer selectedOptionId = submitDTO.selectedAnswers()[i];
                
                // Find if selected option is correct
                boolean isCorrect = question.getOptions().stream()
                        .anyMatch(opt -> opt.getOptionId().equals(selectedOptionId) && opt.getIsCorrect());
                
                if (isCorrect) {
                    correctAnswers++;
                }
            }
            
            // Calculate score
            int score = (int) ((double) correctAnswers / questions.size() * 100);
            
            // Save attempt
            UserQuizAttempt attempt = new UserQuizAttempt();
            attempt.setUserId(userId);
            attempt.setQuiz(quiz);
            attempt.setScore(score);
            attempt.setCorrectAnswers(correctAnswers);
            attempt.setTotalQuestions(questions.size());
            attempt.setPercentage((double) correctAnswers / questions.size() * 100);
            attempt.setPassStatus(score >= 80 ? "PASSED" : "FAILED");
            
            UserQuizAttempt saved = attemptRepository.save(attempt);
            
            QuizScoreDTO result = new QuizScoreDTO(
                    saved.getAttemptId(),
                    quiz.getQuizId(),
                    quiz.getTitle(),
                    score,
                    correctAnswers,
                    questions.size(),
                    saved.getPercentage(),
                    saved.getPassStatus(),
                    saved.getAttemptedAt()
            );
            
            return ResponseEntity.ok(ApiResponse.success(result, "Quiz submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error submitting quiz: " + e.getMessage()));
        }
    }

    /**
     * JL-44: Review incorrect answers after a quiz submission payload
     */
    @PostMapping("/review-wrong")
    public ResponseEntity<ApiResponse<List<WrongAnswerReviewDTO>>> reviewWrongAnswers(@RequestBody SubmitQuizDTO submitDTO) {
        try {
            Quiz quiz = quizRepository.findById(submitDTO.quizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            List<Question> orderedQuestions = quiz.getQuestions().stream()
                    .sorted(Comparator.comparing(Question::getQuestionId))
                    .toList();

            Integer[] selectedAnswers = submitDTO.selectedAnswers();
            if (selectedAnswers == null) {
                selectedAnswers = new Integer[0];
            }

            List<WrongAnswerReviewDTO> wrongAnswers = new ArrayList<>();

            for (int i = 0; i < orderedQuestions.size(); i++) {
                Question question = orderedQuestions.get(i);
                Long selectedOptionId = i < selectedAnswers.length && selectedAnswers[i] != null
                        ? selectedAnswers[i].longValue()
                        : null;

                Option correctOption = question.getOptions().stream()
                        .filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
                        .findFirst()
                        .orElse(null);

                if (correctOption == null) {
                    continue;
                }

                if (!Objects.equals(correctOption.getOptionId(), selectedOptionId)) {
                    Option selectedOption = question.getOptions().stream()
                            .filter(opt -> Objects.equals(opt.getOptionId(), selectedOptionId))
                            .findFirst()
                            .orElse(null);

                    wrongAnswers.add(new WrongAnswerReviewDTO(
                            question.getQuestionId(),
                            question.getText(),
                            selectedOption != null ? selectedOption.getOptionId() : null,
                            selectedOption != null ? selectedOption.getText() : "(Không chọn đáp án)",
                            correctOption.getOptionId(),
                            correctOption.getText()
                    ));
                }
            }

            return ResponseEntity.ok(ApiResponse.success(wrongAnswers, "Wrong answer review generated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error reviewing wrong answers: " + e.getMessage()));
        }
    }

    /**
     * JL-43: Get quiz history for current user
     */
    @GetMapping("/{quizId}/history")
    public ResponseEntity<ApiResponse<List<QuizScoreDTO>>> getQuizHistory(@PathVariable Long quizId) {
        try {
            String userId = getCurrentUserId();
            
            List<UserQuizAttempt> attempts = attemptRepository.findByUserIdAndQuiz_QuizId(userId, quizId);
            
            List<QuizScoreDTO> history = attempts.stream()
                    .map(attempt -> new QuizScoreDTO(
                            attempt.getAttemptId(),
                            attempt.getQuiz().getQuizId(),
                            attempt.getQuiz().getTitle(),
                            attempt.getScore(),
                            attempt.getCorrectAnswers(),
                            attempt.getTotalQuestions(),
                            attempt.getPercentage(),
                            attempt.getPassStatus(),
                            attempt.getAttemptedAt()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(history, "Quiz history retrieved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving quiz history: " + e.getMessage()));
        }
    }

    /**
     * Helper to get current user ID
     */
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
