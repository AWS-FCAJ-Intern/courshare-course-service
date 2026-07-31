package com.courshare.course.api;

import com.courshare.course.api.dto.ReorderRequest;
import com.courshare.course.api.dto.SectionRequest;
import com.courshare.course.api.dto.SectionResponse;
import com.courshare.course.application.SectionService;
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
@RequestMapping("/courses/{courseId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectionResponse createSection(
            @PathVariable String courseId,
            @Valid @RequestBody SectionRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return sectionService.createSection(courseId, request, instructorId);
    }

    @PutMapping("/{sectionId}")
    public SectionResponse updateSection(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            @Valid @RequestBody SectionRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return sectionService.updateSection(courseId, sectionId, request, instructorId);
    }

    @DeleteMapping("/{sectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSection(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        sectionService.deleteSection(courseId, sectionId, instructorId);
    }

    @PostMapping("/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderSections(
            @PathVariable String courseId,
            @Valid @RequestBody ReorderRequest request,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        sectionService.reorderSections(courseId, request, instructorId);
    }
}
