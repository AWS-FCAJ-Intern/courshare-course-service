package com.courshare.course.application;

import com.courshare.course.api.dto.CourseStatsResponse;
import com.courshare.course.api.dto.InstructorSummaryResponse;
import com.courshare.course.domain.Course;
import com.courshare.course.domain.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final CourseRepository courseRepository;

    public DashboardService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public CourseStatsResponse getCourseStats(String courseId, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You do not have permission to view this course's stats");
        }

        // Mock stats since enrollment/payment data reside in other services
        return new CourseStatsResponse(0, BigDecimal.ZERO, 0.0);
    }

    public InstructorSummaryResponse getInstructorSummary(String instructorId) {
        long totalCourses = courseRepository.countByInstructorId(instructorId);

        // Mock stats since enrollment/payment data reside in other services
        return new InstructorSummaryResponse(totalCourses, 0, BigDecimal.ZERO);
    }
}
