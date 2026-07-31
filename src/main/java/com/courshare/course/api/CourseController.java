package com.courshare.course.api;

import com.courshare.course.api.dto.CourseCreateRequest;
import com.courshare.course.api.dto.CourseDetailResponse;
import com.courshare.course.api.dto.CourseResponse;
import com.courshare.course.api.dto.CourseUpdateRequest;
import com.courshare.course.application.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return courseService.createCourse(request, instructorId);
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(
            @PathVariable String id,
            @Valid @RequestBody CourseUpdateRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return courseService.updateCourse(id, request, instructorId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable String id, Authentication authentication) {
        String instructorId = authentication.getName();
        courseService.deleteCourse(id, instructorId);
    }

    @PutMapping("/{id}/publish")
    public CourseResponse publishCourse(@PathVariable String id, Authentication authentication) {
        String instructorId = authentication.getName();
        return courseService.publishCourse(id, instructorId);
    }

    @PutMapping("/{id}/unpublish")
    public CourseResponse unpublishCourse(@PathVariable String id, Authentication authentication) {
        String instructorId = authentication.getName();
        return courseService.unpublishCourse(id, instructorId);
    }

    @GetMapping("/{id}")
    public CourseDetailResponse getCourseDetail(
            @PathVariable String id,
            Authentication authentication
    ) {
        String currentUserId = null;
        String currentUserRole = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUserId = authentication.getName();
            currentUserRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.substring(5))
                    .reduce((a, b) -> a + "," + b)
                    .orElse(null);
        }

        return courseService.getCourseDetail(id, currentUserId, currentUserRole);
    }

    @GetMapping
    public Page<CourseResponse> getCourses(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String instructorId,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        String currentUserId = null;
        String currentUserRole = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUserId = authentication.getName();
            currentUserRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.substring(5))
                    .reduce((a, b) -> a + "," + b)
                    .orElse(null);
        }

        return courseService.getCourses(
                categoryId,
                instructorId,
                published,
                search,
                page,
                size,
                currentUserId,
                currentUserRole
        );
    }
}
