package com.courshare.course.api;

import com.courshare.course.api.dto.CourseCreateRequest;
import com.courshare.course.api.dto.CourseDetailResponse;
import com.courshare.course.api.dto.CourseResponse;
import com.courshare.course.api.dto.CourseUpdateRequest;
import com.courshare.course.application.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private UsernamePasswordAuthenticationToken instructorAuth;
    private UsernamePasswordAuthenticationToken studentAuth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        instructorAuth = new UsernamePasswordAuthenticationToken(
                "inst-1",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))
        );

        studentAuth = new UsernamePasswordAuthenticationToken(
                "student-1",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
    }

    @Test
    void createCourse_ShouldReturnCreated_WhenInstructor() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest(
                "Java Programming",
                "Learn Java from scratch",
                "cat-1",
                BigDecimal.valueOf(99.99)
        );
        CourseResponse response = new CourseResponse(
                "course-1",
                "Java Programming",
                "Learn Java from scratch",
                "inst-1",
                "cat-1",
                "Development",
                BigDecimal.valueOf(99.99),
                false,
                Instant.now()
        );

        Mockito.when(courseService.createCourse(any(CourseCreateRequest.class), eq("inst-1")))
                .thenReturn(response);

        mockMvc.perform(post("/courses")
                        .principal(instructorAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("course-1"))
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.instructorId").value("inst-1"));
    }

    @Test
    void updateCourse_ShouldReturnOk_WhenInstructor() throws Exception {
        CourseUpdateRequest request = new CourseUpdateRequest(
                "Java Advanced",
                "Advanced Java concepts",
                "cat-1",
                BigDecimal.valueOf(149.99)
        );
        CourseResponse response = new CourseResponse(
                "course-1",
                "Java Advanced",
                "Advanced Java concepts",
                "inst-1",
                "cat-1",
                "Development",
                BigDecimal.valueOf(149.99),
                false,
                Instant.now()
        );

        Mockito.when(courseService.updateCourse(eq("course-1"), any(CourseUpdateRequest.class), eq("inst-1")))
                .thenReturn(response);

        mockMvc.perform(put("/courses/course-1")
                        .principal(instructorAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Advanced"))
                .andExpect(jsonPath("$.price").value(149.99));
    }

    @Test
    void deleteCourse_ShouldReturnNoContent_WhenInstructor() throws Exception {
        Mockito.doNothing().when(courseService).deleteCourse("course-1", "inst-1");

        mockMvc.perform(delete("/courses/course-1")
                        .principal(instructorAuth))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCourseDetail_ShouldReturnCourse_WhenPublic() throws Exception {
        CourseDetailResponse response = new CourseDetailResponse(
                "course-1",
                "Java Programming",
                "Learn Java from scratch",
                "inst-1",
                "cat-1",
                "Development",
                BigDecimal.valueOf(99.99),
                true,
                Instant.now(),
                Collections.emptyList()
        );

        Mockito.when(courseService.getCourseDetail(eq("course-1"), any(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/courses/course-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("course-1"))
                .andExpect(jsonPath("$.title").value("Java Programming"));
    }

    @Test
    void getCourses_ShouldReturnPagedCourses_WhenPublic() throws Exception {
        CourseResponse response = new CourseResponse(
                "course-1",
                "Java Programming",
                "Learn Java from scratch",
                "inst-1",
                "cat-1",
                "Development",
                BigDecimal.valueOf(99.99),
                true,
                Instant.now()
        );

        Mockito.when(courseService.getCourses(
                        any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("course-1"))
                .andExpect(jsonPath("$.content[0].title").value("Java Programming"));
    }
}
