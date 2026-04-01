package com.japaneseLearning.service;

import com.japaneseLearning.dto.LessonDTO;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.exception.ResourceNotFoundException;
import com.japaneseLearning.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LessonService {
    
    private final LessonRepository lessonRepository;
    private final CourseService courseService;

    public LessonService(LessonRepository lessonRepository, CourseService courseService) {
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
    }

    public LessonDTO getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
    }

    public Lesson getLessonEntityById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourse_CourseIdOrderByOrderInCourseAsc(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * JL-36: Get next lesson in course route from current lesson id.
     */
    public Optional<LessonDTO> getNextLesson(Long currentLessonId) {
        Lesson currentLesson = lessonRepository.findById(currentLessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + currentLessonId));

        List<Lesson> orderedLessons = lessonRepository.findByCourse_CourseIdOrderByOrderInCourseAsc(
                currentLesson.getCourse().getCourseId()
        );

        for (int i = 0; i < orderedLessons.size(); i++) {
            if (orderedLessons.get(i).getLessonId().equals(currentLessonId)) {
                if (i + 1 < orderedLessons.size()) {
                    return Optional.of(convertToDTO(orderedLessons.get(i + 1)));
                }
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public LessonDTO createLesson(LessonDTO lessonDTO) {
        validateLessonDTO(lessonDTO);

        Lesson lesson = new Lesson();
        lesson.setCourse(courseService.getCourseEntityById(lessonDTO.courseId()));
        lesson.setTitle(lessonDTO.title());
        lesson.setContent(lessonDTO.content());
        lesson.setOrderInCourse(lessonDTO.orderInCourse());
        lesson.setVocabYouTubeLink(lessonDTO.vocabYouTubeLink());
        lesson.setGrammarYouTubeLink(lessonDTO.grammarYouTubeLink());
        Lesson savedLesson = lessonRepository.save(lesson);
        return convertToDTO(savedLesson);
    }

    public LessonDTO updateLesson(Long lessonId, LessonDTO lessonDTO) {
        validateLessonDTO(lessonDTO);

        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        
        lesson.setCourse(courseService.getCourseEntityById(lessonDTO.courseId()));
        lesson.setTitle(lessonDTO.title());
        lesson.setContent(lessonDTO.content());
        lesson.setOrderInCourse(lessonDTO.orderInCourse());
        lesson.setVocabYouTubeLink(lessonDTO.vocabYouTubeLink());
        lesson.setGrammarYouTubeLink(lessonDTO.grammarYouTubeLink());
        
        Lesson updatedLesson = lessonRepository.save(lesson);
        return convertToDTO(updatedLesson);
    }

    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson not found with id: " + lessonId);
        }
        lessonRepository.deleteById(lessonId);
    }

    private void validateLessonDTO(LessonDTO lessonDTO) {
        if (lessonDTO == null) {
            throw new IllegalArgumentException("Lesson payload is required");
        }
        if (lessonDTO.courseId() == null) {
            throw new IllegalArgumentException("courseId is required");
        }
        if (lessonDTO.title() == null || lessonDTO.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Lesson title is required");
        }
    }

    private LessonDTO convertToDTO(Lesson lesson) {
        return new LessonDTO(
                lesson.getLessonId(),
                lesson.getCourse() != null ? lesson.getCourse().getCourseId() : null,
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getOrderInCourse(),
                lesson.getVocabYouTubeLink(),
                lesson.getGrammarYouTubeLink()
        );
    }
}
