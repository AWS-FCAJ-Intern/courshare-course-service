package com.courshare.course.application;

import com.courshare.course.api.dto.CourseCreateRequest;
import com.courshare.course.api.dto.CourseDetailResponse;
import com.courshare.course.api.dto.CourseResponse;
import com.courshare.course.api.dto.CourseUpdateRequest;
import com.courshare.course.api.dto.LessonResponse;
import com.courshare.course.api.dto.SectionResponse;
import com.courshare.course.domain.CategoryRepository;
import com.courshare.course.domain.Course;
import com.courshare.course.domain.CourseRepository;
import com.courshare.course.domain.Lesson;
import com.courshare.course.domain.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request, String instructorId) {
        if (StringUtils.hasText(request.categoryId()) && !categoryRepository.existsById(request.categoryId())) {
            throw new NotFoundException("Category not found with id: " + request.categoryId());
        }

        Course course = new Course(
                request.title(),
                request.description(),
                instructorId,
                request.categoryId(),
                request.price()
        );
        course.setPublished(true);

        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Transactional
    public CourseResponse updateCourse(String id, CourseUpdateRequest request, String instructorId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

        verifyOwnership(course, instructorId);

        if (StringUtils.hasText(request.categoryId()) && !categoryRepository.existsById(request.categoryId())) {
            throw new NotFoundException("Category not found with id: " + request.categoryId());
        }

        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setCategoryId(request.categoryId());
        course.setPrice(request.price());

        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Transactional
    public void deleteCourse(String id, String instructorId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

        verifyOwnership(course, instructorId);

        // TODO: In later phases, check if any enrollments exist before deleting.
        // Currently, we just delete the course.
        courseRepository.delete(course);
    }

    @Transactional
    public CourseResponse publishCourse(String id, String instructorId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

        verifyOwnership(course, instructorId);
        course.setPublished(true);
        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Transactional
    public CourseResponse unpublishCourse(String id, String instructorId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

        verifyOwnership(course, instructorId);
        course.setPublished(false);
        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    public CourseDetailResponse getCourseDetail(String id, String currentUserId, String currentUserRole) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

        if (!course.isPublished()) {
            boolean isOwner = currentUserId != null && currentUserId.equals(course.getInstructorId());
            boolean isAdmin = currentUserRole != null && currentUserRole.contains("ADMIN");
            if (!isOwner && !isAdmin) {
                throw new ForbiddenException("You do not have permission to view this unpublished course");
            }
        }

        return mapToCourseDetailResponse(course);
    }

    public Page<CourseResponse> getCourses(
            String categoryId,
            String instructorId,
            Boolean published,
            String search,
            int page,
            int size,
            String currentUserId,
            String currentUserRole
    ) {
        Pageable pageable = PageRequest.of(page, size);
        String searchParam = StringUtils.hasText(search) ? search.trim() : null;
        String categoryParam = StringUtils.hasText(categoryId) ? categoryId : null;

        // If specific instructor search
        if (StringUtils.hasText(instructorId)) {
            boolean isSelf = currentUserId != null && currentUserId.equals(instructorId);
            boolean isAdmin = currentUserRole != null && currentUserRole.contains("ADMIN");

            if (isSelf || isAdmin) {
                // Return all courses (published or draft) for this instructor
                return courseRepository.searchInstructorCourses(instructorId, categoryParam, published, searchParam, pageable)
                        .map(this::mapToCourseResponse);
            } else {
                // Public view of this instructor: force published = true
                return courseRepository.searchInstructorCourses(instructorId, categoryParam, true, searchParam, pageable)
                        .map(this::mapToCourseResponse);
            }
        }

        // General search: only published courses are visible publicly
        return courseRepository.searchPublished(categoryParam, searchParam, pageable)
                .map(this::mapToCourseResponse);
    }

    private void verifyOwnership(Course course, String instructorId) {
        if (!course.getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You do not have permission to modify this course");
        }
    }

    private CourseResponse mapToCourseResponse(Course c) {
        String categoryName = c.getCategory() != null ? c.getCategory().getName() : null;
        return new CourseResponse(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getInstructorId(),
                c.getCategoryId(),
                categoryName,
                c.getPrice(),
                c.isPublished(),
                c.getCreatedAt()
        );
    }

    private CourseDetailResponse mapToCourseDetailResponse(Course c) {
        String categoryName = c.getCategory() != null ? c.getCategory().getName() : null;
        var sectionResponses = c.getSections().stream()
                .map(this::mapToSectionResponse)
                .toList();

        return new CourseDetailResponse(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getInstructorId(),
                c.getCategoryId(),
                categoryName,
                c.getPrice(),
                c.isPublished(),
                c.getCreatedAt(),
                sectionResponses
        );
    }

    private SectionResponse mapToSectionResponse(Section s) {
        var lessonResponses = s.getLessons().stream()
                .map(this::mapToLessonResponse)
                .toList();

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
