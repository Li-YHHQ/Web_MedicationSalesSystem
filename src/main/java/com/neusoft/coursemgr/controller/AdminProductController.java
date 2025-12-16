package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.*;
import com.neusoft.coursemgr.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "管理员-药品管理")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "管理员药品列表")
    public ApiResponse<List<Product>> list(
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        Long adminId = AdminGuard.requireAdminUserId();
        return ApiResponse.ok(productService.adminList(adminId, categoryId, keyword));
    }

    @PostMapping
    @Operation(summary = "管理员新增药品")
    public ApiResponse<Long> create(@Valid @RequestBody CreateProductRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        Product p = new Product();
        p.setCategoryId(req.getCategoryId());
        p.setName(req.getName());
        p.setSubName(req.getSubName());
        p.setManufacturer(req.getManufacturer());
        p.setSpecification(req.getSpecification());
        p.setApprovalNumber(req.getApprovalNumber());
        p.setIsPrescription(req.getIsPrescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setCoverUrl(req.getCoverUrl());
        p.setDescription(req.getDescription());
        p.setStatus(1);
        return ApiResponse.ok("created", productService.adminCreate(adminId, p));
    }

    @PutMapping("/{id}")
    @Operation(summary = "管理员更新药品")
    public ApiResponse<String> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        Product p = new Product();
        p.setId(id);
        p.setCategoryId(req.getCategoryId());
        p.setName(req.getName());
        p.setSubName(req.getSubName());
        p.setManufacturer(req.getManufacturer());
        p.setSpecification(req.getSpecification());
        p.setApprovalNumber(req.getApprovalNumber());
        p.setIsPrescription(req.getIsPrescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setCoverUrl(req.getCoverUrl());
        p.setDescription(req.getDescription());
        p.setStatus(req.getStatus());
        productService.adminUpdate(adminId, p);
        return ApiResponse.ok("updated", "success");
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "管理员上/下架药品")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateProductStatusRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        productService.adminUpdateStatus(adminId, id, req.getStatus());
        return ApiResponse.ok("updated", "success");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "管理员删除药品")
    public ApiResponse<String> delete(@PathVariable Long id) {
        Long adminId = AdminGuard.requireAdminUserId();
        productService.adminDelete(adminId, id);
        return ApiResponse.ok("deleted", "success");
    }
}
