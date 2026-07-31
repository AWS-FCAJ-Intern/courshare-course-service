package com.courshare.course.api.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReorderRequest(
        @NotEmpty(message = "List of IDs for reordering must not be empty")
        List<String> ids
) {
}
