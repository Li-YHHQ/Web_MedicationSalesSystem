package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.CreateOrderRequest;
import com.neusoft.coursemgr.domain.Order;
import com.neusoft.coursemgr.domain.OrderDetail;
import com.neusoft.coursemgr.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "从购物车生成订单")
    public ApiResponse<Long> create(@Valid @RequestBody CreateOrderRequest req) {
        Long userId = AdminGuard.requireLoginUserId();
        return ApiResponse.ok("created", orderService.createFromCart(userId, req));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "模拟支付")
    public ApiResponse<String> pay(@PathVariable("id") Long orderId) {
        Long userId = AdminGuard.requireLoginUserId();
        orderService.pay(userId, orderId);
        return ApiResponse.ok("paid", "success");
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "确认收货")
    public ApiResponse<String> receive(@PathVariable("id") Long orderId) {
        Long userId = AdminGuard.requireLoginUserId();
        orderService.receive(userId, orderId);
        return ApiResponse.ok("received", "success");
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public ApiResponse<String> cancel(@PathVariable("id") Long orderId) {
        Long userId = AdminGuard.requireLoginUserId();
        orderService.cancel(userId, orderId);
        return ApiResponse.ok("canceled", "success");
    }

    @GetMapping
    @Operation(summary = "查看我的订单")
    public ApiResponse<List<Order>> myOrders(@Parameter(description = "订单状态") @RequestParam(required = false) String status) {
        Long userId = AdminGuard.requireLoginUserId();
        return ApiResponse.ok(orderService.listMyOrders(userId, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查看我的订单详情")
    public ApiResponse<OrderDetail> detail(@PathVariable("id") Long orderId) {
        Long userId = AdminGuard.requireLoginUserId();
        return ApiResponse.ok(orderService.getMyOrderDetail(userId, orderId));
    }
}
