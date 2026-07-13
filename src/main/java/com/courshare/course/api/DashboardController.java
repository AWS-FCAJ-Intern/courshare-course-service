package com.courshare.course.api;

import com.courshare.course.api.dto.CourseStatsResponse;
import com.courshare.course.api.dto.InstructorSummaryResponse;
import com.courshare.course.application.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/courses/{id}/stats")
    public CourseStatsResponse getCourseStats(
            @PathVariable String id,
            Authentication authentication
    ) {
        String instructorId = authentication.getName();
        return dashboardService.getCourseStats(id, instructorId);
    }

    @GetMapping("/instructor/summary")
    public InstructorSummaryResponse getInstructorSummary(Authentication authentication) {
        String instructorId = authentication.getName();
        return dashboardService.getInstructorSummary(instructorId);
    }
}
