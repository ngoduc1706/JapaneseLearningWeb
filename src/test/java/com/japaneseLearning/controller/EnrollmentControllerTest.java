import com.japaneseLearning.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(...); // Set up SecurityContextHolder as needed.
    }

    @Test
    public void testEnrollUser() throws Exception {
        Long userId = 1L;
        Long courseId = 1L;
        doReturn(true).when(enrollmentService).enrollUser(userId, courseId);

        mockMvc.perform(post("/api/enrollment/{userId}/{courseId}", userId, courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(enrollmentService).enrollUser(userId, courseId);
    }

    @Test
    public void testCheckEnrollment() throws Exception {
        Long courseId = 1L;
        doReturn(true).when(enrollmentService).checkEnrollment(courseId);

        mockMvc.perform(get("/api/enrollment/check/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(enrollmentService).checkEnrollment(courseId);
    }

    @Test
    public void testFreeEnrollment() throws Exception {
        Long courseId = 1L;
        doReturn(true).when(enrollmentService).enrollFree(courseId);

        mockMvc.perform(post("/api/enrollment/free/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(enrollmentService).enrollFree(courseId);
    }
}