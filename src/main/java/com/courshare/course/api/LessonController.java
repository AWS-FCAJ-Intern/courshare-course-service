package com.courshare.course.api;

import com.courshare.course.api.dto.LessonRequest;
import com.courshare.course.api.dto.LessonResponse;
import com.courshare.course.api.dto.ReorderRequest;
import com.courshare.course.application.LessonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses/{courseId}/sections/{sectionId}/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonResponse createLesson(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            @Valid @RequestBody LessonRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return lessonService.createLesson(courseId, sectionId, request, instructorId);
    }

    @PutMapping("/{lessonId}")
    public LessonResponse updateLesson(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            @PathVariable String lessonId,
            @Valid @RequestBody LessonRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return lessonService.updateLesson(courseId, sectionId, lessonId, request, instructorId);
    }

    @DeleteMapping("/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            @PathVariable String lessonId,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        lessonService.deleteLesson(courseId, sectionId, lessonId, instructorId);
    }

    @PostMapping("/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderLessons(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            @Valid @RequestBody ReorderRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        lessonService.reorderLessons(courseId, sectionId, request, instructorId);
    }
}
