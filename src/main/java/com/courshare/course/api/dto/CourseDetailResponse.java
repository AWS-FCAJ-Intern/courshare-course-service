package com.courshare.course.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CourseDetailResponse(
        String id,
        String title,
        String description,
        String instructorId,
        String categoryId,
        String categoryName,
        BigDecimal price,
        boolean published,
        Instant createdAt,
        List<SectionResponse> sections
) {
}
