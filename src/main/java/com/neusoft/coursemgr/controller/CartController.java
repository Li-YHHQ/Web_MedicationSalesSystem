package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.AddToCartRequest;
import com.neusoft.coursemgr.domain.CartView;
import com.neusoft.coursemgr.domain.UpdateCartItemRequest;
import com.neusoft.coursemgr.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "购物车")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    @Operation(summary = "添加至购物车")
    public ApiResponse<String> add(@Valid @RequestBody AddToCartRequest req) {
        Long userId = AdminGuard.requireLoginUserId();
        cartService.addItem(userId, req.getProductId(), req.getQuantity());
        return ApiResponse.ok("added", "success");
    }

    @GetMapping
    @Operation(summary = "查看购物车")
    public ApiResponse<CartView> get() {
        Long userId = AdminGuard.requireLoginUserId();
        return ApiResponse.ok(cartService.getCart(userId));
    }

    @PatchMapping("/items/{itemId}")
    @Operation(summary = "修改购物车数量")
    public ApiResponse<String> updateQty(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest req) {
        Long userId = AdminGuard.requireLoginUserId();
        cartService.updateItemQuantity(userId, itemId, req.getQuantity());
        return ApiResponse.ok("updated", "success");
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "移除购物车条目")
    public ApiResponse<String> remove(@PathVariable Long itemId) {
        Long userId = AdminGuard.requireLoginUserId();
        cartService.removeItem(userId, itemId);
        return ApiResponse.ok("deleted", "success");
    }

    @DeleteMapping
    @Operation(summary = "清空购物车")
    public ApiResponse<String> clear() {
        Long userId = AdminGuard.requireLoginUserId();
        cartService.clear(userId);
        return ApiResponse.ok("cleared", "success");
    }
}
