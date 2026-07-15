package com.courshare.course.api.dto;

public record LessonResponse(
        String id,
        String sectionId,
        String title,
        String description,
        String type,
        String videoUrl,
        String mediaUrl,
        String content,
        int orderIndex
) {
}
