package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.FinanceDaily;
import com.neusoft.coursemgr.domain.FinanceSummaryVO;
import com.neusoft.coursemgr.mapper.FinanceDailyMapper;
import com.neusoft.coursemgr.service.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@Tag(name = "财务统计")
public class FinanceController {

    private final FinanceService financeService;
    private final FinanceDailyMapper financeDailyMapper;

    public FinanceController(FinanceService financeService, FinanceDailyMapper financeDailyMapper) {
        this.financeService = financeService;
        this.financeDailyMapper = financeDailyMapper;
    }

    @GetMapping("/summary")
    @Operation(summary = "查询财务汇总（默认本月）")
    public ApiResponse<FinanceSummaryVO> getSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        if (startDate == null || startDate.isBlank()) {
            startDate = LocalDate.now().withDayOfMonth(1).toString();
        }
        if (endDate == null || endDate.isBlank()) {
            endDate = LocalDate.now().toString();
        }
        return ApiResponse.ok(financeService.getSummary(startDate, endDate));
    }

    @GetMapping("/daily")
    @Operation(summary = "查询每日财务明细列表（默认本月）")
    public ApiResponse<List<FinanceDaily>> getDaily(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        if (startDate == null || startDate.isBlank()) {
            startDate = LocalDate.now().withDayOfMonth(1).toString();
        }
        if (endDate == null || endDate.isBlank()) {
            endDate = LocalDate.now().toString();
        }
        return ApiResponse.ok(financeDailyMapper.selectList(startDate, endDate));
    }

    @PostMapping("/sync")
    @Operation(summary = "手动触发同步今日财务数据")
    public ApiResponse<String> syncToday() {
        financeService.syncToday();
        return ApiResponse.ok("synced", "success");
    }
}
