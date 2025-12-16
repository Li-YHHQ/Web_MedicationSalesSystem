package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.Product;
import com.neusoft.coursemgr.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "药品（公开）")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "药品列表/搜索/筛选")
    public ApiResponse<List<Product>> list(
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "最小价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最大价格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "是否处方药(0/1)") @RequestParam(required = false) Integer isPrescription
    ) {
        return ApiResponse.ok(productService.listPublic(categoryId, keyword, minPrice, maxPrice, isPrescription));
    }

    @GetMapping("/{id}")
    @Operation(summary = "药品详情")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.getDetail(id));
    }
}
