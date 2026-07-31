package com.courshare.course.api.dto;

import java.math.BigDecimal;

public record CourseStatsResponse(
        int studentsCount,
        BigDecimal revenue,
        double rating
) {
}
