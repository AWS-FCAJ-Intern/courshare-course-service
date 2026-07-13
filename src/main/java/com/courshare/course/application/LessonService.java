package com.courshare.course.application;

import com.courshare.course.api.dto.LessonRequest;
import com.courshare.course.api.dto.LessonResponse;
import com.courshare.course.api.dto.ReorderRequest;
import com.courshare.course.domain.Course;
import com.courshare.course.domain.CourseRepository;
import com.courshare.course.domain.Lesson;
import com.courshare.course.domain.LessonRepository;
import com.courshare.course.domain.Section;
import com.courshare.course.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public LessonService(
            LessonRepository lessonRepository,
            SectionRepository sectionRepository,
            CourseRepository courseRepository
    ) {
        this.lessonRepository = lessonRepository;
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public LessonResponse createLesson(
            String courseId,
            String sectionId,
            LessonRequest request,
            String instructorId
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourseId().equals(courseId)) {
            throw new ConflictException("Section does not belong to the specified course");
        }

        Lesson lesson = new Lesson(
                sectionId,
                request.title(),
                request.description(),
                request.videoUrl(),
                request.orderIndex()
        );

        lesson = lessonRepository.save(lesson);
        return mapToLessonResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(
            String courseId,
            String sectionId,
            String lessonId,
            LessonRequest request,
            String instructorId
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourseId().equals(courseId)) {
            throw new ConflictException("Section does not belong to the specified course");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found with id: " + lessonId));

        if (!lesson.getSectionId().equals(sectionId)) {
            throw new ConflictException("Lesson does not belong to the specified section");
        }

        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setOrderIndex(request.orderIndex());

        lesson = lessonRepository.save(lesson);
        return mapToLessonResponse(lesson);
    }

    @Transactional
    public void deleteLesson(
            String courseId,
            String sectionId,
            String lessonId,
            String instructorId
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourseId().equals(courseId)) {
            throw new ConflictException("Section does not belong to the specified course");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found with id: " + lessonId));

        if (!lesson.getSectionId().equals(sectionId)) {
            throw new ConflictException("Lesson does not belong to the specified section");
        }

        lessonRepository.delete(lesson);
    }

    @Transactional
    public void reorderLessons(
            String courseId,
            String sectionId,
            ReorderRequest request,
            String instructorId
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        verifyOwnership(course, instructorId);

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourseId().equals(courseId)) {
            throw new ConflictException("Section does not belong to the specified course");
        }

        List<String> lessonIds = request.ids();
        for (int i = 0; i < lessonIds.size(); i++) {
            String lessonId = lessonIds.get(i);
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new NotFoundException("Lesson not found with id: " + lessonId));

            if (!lesson.getSectionId().equals(sectionId)) {
                throw new ConflictException("Lesson " + lessonId + " does not belong to the specified section");
            }

            lesson.setOrderIndex(i);
            lessonRepository.save(lesson);
        }
    }

    private void verifyOwnership(Course course, String instructorId) {
        if (!course.getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You do not have permission to modify this course content");
        }
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
