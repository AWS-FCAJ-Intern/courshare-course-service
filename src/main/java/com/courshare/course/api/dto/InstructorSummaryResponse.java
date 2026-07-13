package com.courshare.course.api.dto;

import java.math.BigDecimal;

public record InstructorSummaryResponse(
        long totalCourses,
        int totalStudents,
        BigDecimal totalRevenue
) {
}
