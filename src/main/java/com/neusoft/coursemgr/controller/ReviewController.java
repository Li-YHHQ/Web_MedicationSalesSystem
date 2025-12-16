package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.CreateReviewRequest;
import com.neusoft.coursemgr.domain.Review;
import com.neusoft.coursemgr.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "评价")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @Operation(summary = "创建评价（仅已完成订单）")
    public ApiResponse<Long> create(@Valid @RequestBody CreateReviewRequest req) {
        Long userId = AdminGuard.requireLoginUserId();
        return ApiResponse.ok("created", reviewService.create(userId, req.getOrderId(), req.getProductId(), req.getRating(), req.getContent()));
    }

    @GetMapping("/products/{id}/reviews")
    @Operation(summary = "查看商品评价列表")
    public ApiResponse<List<Review>> list(
            @Parameter(description = "商品ID") @PathVariable("id") Long productId
    ) {
        return ApiResponse.ok(reviewService.listByProduct(productId));
    }
}
