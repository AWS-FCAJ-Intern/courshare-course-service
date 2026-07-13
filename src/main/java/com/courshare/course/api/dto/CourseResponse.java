package com.courshare.course.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CourseResponse(
        String id,
        String title,
        String description,
        String instructorId,
        String categoryId,
        String categoryName,
        BigDecimal price,
        boolean published,
        Instant createdAt
) {
}
