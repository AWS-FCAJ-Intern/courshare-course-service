package com.courshare.course.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CourseUpdateRequest(
        @NotBlank(message = "Course title is required")
        @Size(max = 200, message = "Course title must not exceed 200 characters")
        String title,

        String description,

        String categoryId,

        @NotNull(message = "Course price is required")
        @DecimalMin(value = "0.0", message = "Course price must be greater than or equal to 0")
        BigDecimal price
) {
}
