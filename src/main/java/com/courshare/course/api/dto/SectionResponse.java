package com.courshare.course.api.dto;

import java.util.List;

public record SectionResponse(
        String id,
        String courseId,
        String title,
        int orderIndex,
        List<LessonResponse> lessons
) {
}
