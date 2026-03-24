package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.StockOutRequest;
import com.neusoft.coursemgr.domain.StockOutVO;
import com.neusoft.coursemgr.service.StockOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "出库管理")
public class StockOutController {

    private final StockOutService stockOutService;

    public StockOutController(StockOutService stockOutService) {
        this.stockOutService = stockOutService;
    }

    @PostMapping("/out")
    @Operation(summary = "药品出库")
    public ApiResponse<String> stockOut(@Valid @RequestBody StockOutRequest req) {
        stockOutService.stockOut(req);
        return ApiResponse.ok("stocked", "success");
    }

    @GetMapping("/out")
    @Operation(summary = "分页查询出库记录")
    public ApiResponse<PageResult<StockOutVO>> listStockOut(
            @RequestParam(required = false) Integer drugId,
            @RequestParam(required = false) Integer outType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;
        return ApiResponse.ok(stockOutService.listStockOut(drugId, outType, startDate, endDate, page, size));
    }
}
