package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.DashboardVO;
import com.neusoft.coursemgr.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "数据看板")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "获取看板数据")
    public ApiResponse<DashboardVO> getDashboard() {
        return ApiResponse.ok(dashboardService.getDashboard());
    }
}
