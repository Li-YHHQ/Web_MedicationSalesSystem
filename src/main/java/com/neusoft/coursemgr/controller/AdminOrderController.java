package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.Order;
import com.neusoft.coursemgr.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "管理员-订单管理")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "管理员订单列表")
    public ApiResponse<List<Order>> list(
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        Long adminId = AdminGuard.requireAdminUserId();
        return ApiResponse.ok(orderService.adminListOrders(adminId, status, keyword));
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "管理员发货")
    public ApiResponse<String> ship(@PathVariable("id") Long orderId) {
        Long adminId = AdminGuard.requireAdminUserId();
        orderService.adminShip(adminId, orderId);
        return ApiResponse.ok("shipped", "success");
    }
}
