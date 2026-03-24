package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.StockBatch;
import com.neusoft.coursemgr.domain.StockInRequest;
import com.neusoft.coursemgr.domain.StockInVO;
import com.neusoft.coursemgr.service.StockInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "入库管理")
public class StockInController {

    private final StockInService stockInService;

    public StockInController(StockInService stockInService) {
        this.stockInService = stockInService;
    }

    @PostMapping("/in")
    @Operation(summary = "药品入库")
    public ApiResponse<String> stockIn(@Valid @RequestBody StockInRequest req) {
        stockInService.stockIn(req);
        return ApiResponse.ok("stocked", "success");
    }

    @GetMapping("/in")
    @Operation(summary = "分页查询入库记录")
    public ApiResponse<PageResult<StockInVO>> listStockIn(
            @RequestParam(required = false) Integer drugId,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;
        return ApiResponse.ok(stockInService.listStockIn(drugId, supplierId, startDate, endDate, page, size));
    }

    @GetMapping("/batches/{drugId}")
    @Operation(summary = "查询某药品的所有批次")
    public ApiResponse<List<StockBatch>> getBatchesByDrugId(@PathVariable Integer drugId) {
        return ApiResponse.ok(stockInService.getBatchesByDrugId(drugId));
    }

    @GetMapping("/expire")
    @Operation(summary = "查询即将过期药品批次")
    public ApiResponse<List<StockBatch>> getExpiringSoon(
            @RequestParam(defaultValue = "90") int days) {
        return ApiResponse.ok(stockInService.getExpiringSoon(days));
    }

    @GetMapping("/low")
    @Operation(summary = "查询库存不足药品批次")
    public ApiResponse<List<StockBatch>> getLowStock() {
        return ApiResponse.ok(stockInService.getLowStock());
    }
}
