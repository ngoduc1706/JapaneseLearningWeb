import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.japaneseLearning.entity.Course;
import com.japaneseLearning.entity.UserCourseEnrollment;
import com.japaneseLearning.repository.CourseRepository;
import com.japaneseLearning.repository.UserCourseEnrollmentRepository;
import com.japaneseLearning.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

class EnrollmentServiceTest {

    @Mock
    private UserCourseEnrollmentRepository userCourseEnrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void enrollUserInCourse_whenCourseExistsAndUserNotEnrolled_shouldEnrollUser() {
        Long userId = 1L;
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourse(course);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userCourseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());
        when(userCourseEnrollmentRepository.save(any(UserCourseEnrollment.class))).thenReturn(enrollment);

        UserCourseEnrollment result = enrollmentService.enrollUserInCourse(userId, courseId);

        assertThat(result).isEqualTo(enrollment);
        verify(userCourseEnrollmentRepository).save(any(UserCourseEnrollment.class));
    }

    @Test
    void enrollUserInCourse_whenCourseDoesNotExist_shouldThrowException() {
        Long userId = 1L;
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            enrollmentService.enrollUserInCourse(userId, courseId);
        });

        assertThat(thrown).hasMessageContaining("Course not found with id: " + courseId);
    }

    @Test
    void enrollUserInCourse_whenUserAlreadyEnrolled_shouldReturnExistingEnrollment() {
        Long userId = 1L;
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourse(course);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userCourseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(enrollment));

        UserCourseEnrollment result = enrollmentService.enrollUserInCourse(userId, courseId);

        assertThat(result).isEqualTo(enrollment);
        verify(userCourseEnrollmentRepository, never()).save(any(UserCourseEnrollment.class));
    }

    @Test
    void completeEnrollment_whenEnrollmentExists_shouldUpdateStatus() {
        Long enrollmentId = 1L;
        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setId(enrollmentId);
        enrollment.setStatus("PENDING");

        when(userCourseEnrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        enrollmentService.completeEnrollment(enrollmentId);

        assertThat(enrollment.getStatus()).isEqualTo("COMPLETED");
        assertThat(enrollment.getCompletedAt()).isNotNull();
        verify(userCourseEnrollmentRepository).save(enrollment);
    }

    @Test
    void completeEnrollment_whenEnrollmentDoesNotExist_shouldThrowException() {
        Long enrollmentId = 1L;

        when(userCourseEnrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            enrollmentService.completeEnrollment(enrollmentId);
        });

        assertThat(thrown).hasMessageContaining("Enrollment not found");
    }

    @Test
    void isUserEnrolledInCourse_whenEnrollmentExists_shouldReturnTrue() {
        Long userId = 1L;
        Long courseId = 1L;
        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourse(new Course(){ { setId(courseId); }});

        when(userCourseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(enrollment));

        boolean result = enrollmentService.isUserEnrolledInCourse(userId, courseId);

        assertThat(result).isTrue();
    }

    @Test
    void isUserEnrolledInCourse_whenEnrollmentDoesNotExist_shouldReturnFalse() {
        Long userId = 1L;
        Long courseId = 1L;

        when(userCourseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());

        boolean result = enrollmentService.isUserEnrolledInCourse(userId, courseId);

        assertThat(result).isFalse();
    }
}