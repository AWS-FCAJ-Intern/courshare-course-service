package com.courshare.course.application;

import com.courshare.course.api.dto.LessonResponse;
import com.courshare.course.api.dto.ReorderRequest;
import com.courshare.course.api.dto.SectionRequest;
import com.courshare.course.api.dto.SectionResponse;
import com.courshare.course.domain.Course;
import com.courshare.course.domain.CourseRepository;
import com.courshare.course.domain.Lesson;
import com.courshare.course.domain.Section;
import com.courshare.course.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public SectionResponse createSection(String courseId, SectionRequest request, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = new Section(courseId, request.title(), request.orderIndex());
        section = sectionRepository.save(section);
        return mapToSectionResponse(section);
    }

    @Transactional
    public SectionResponse updateSection(String courseId, String sectionId, SectionRequest request, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourseId().equals(courseId)) {
            throw new ConflictException("Section does not belong to the specified course");
        }

        section.setTitle(request.title());
        section.setOrderIndex(request.orderIndex());
        section = sectionRepository.save(section);
        return mapToSectionResponse(section);
    }

    @Transactional
    public void deleteSection(String courseId, String sectionId, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourseId().equals(courseId)) {
            throw new ConflictException("Section does not belong to the specified course");
        }

        sectionRepository.delete(section);
    }

    @Transactional
    public void reorderSections(String courseId, ReorderRequest request, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        List<String> sectionIds = request.ids();
        for (int i = 0; i < sectionIds.size(); i++) {
            String sectionId = sectionIds.get(i);
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

            if (!section.getCourseId().equals(courseId)) {
                throw new ConflictException("Section " + sectionId + " does not belong to the specified course");
            }

            section.setOrderIndex(i);
            sectionRepository.save(section);
        }
    }

    private void verifyOwnership(Course course, String instructorId) {
        if (!course.getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You do not have permission to modify this course content");
        }
    }

    private SectionResponse mapToSectionResponse(Section s) {
        List<LessonResponse> lessonResponses = s.getLessons() != null ? s.getLessons().stream()
                .map(this::mapToLessonResponse)
                .toList() : Collections.emptyList();

        return new SectionResponse(
                s.getId(),
                s.getCourseId(),
                s.getTitle(),
                s.getOrderIndex(),
                lessonResponses
        );
    }

    private LessonResponse mapToLessonResponse(Lesson l) {
        return new LessonResponse(
                l.getId(),
                l.getSectionId(),
                l.getTitle(),
                l.getDescription(),
                l.getVideoUrl(),
                l.getOrderIndex()
        );
    }
}
