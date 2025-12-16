package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.Category;
import com.neusoft.coursemgr.domain.CreateCategoryRequest;
import com.neusoft.coursemgr.domain.UpdateCategoryRequest;
import com.neusoft.coursemgr.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "管理员-分类管理")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "管理员获取分类列表")
    public ApiResponse<List<Category>> listAll() {
        Long adminId = AdminGuard.requireAdminUserId();
        return ApiResponse.ok(categoryService.adminListAll(adminId));
    }

    @PostMapping
    @Operation(summary = "管理员新增分类")
    public ApiResponse<Long> create(@Valid @RequestBody CreateCategoryRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        return ApiResponse.ok("created", categoryService.adminCreate(adminId, req.getName(), req.getSortOrder()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "管理员更新分类")
    public ApiResponse<String> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        categoryService.adminUpdate(adminId, id, req.getName(), req.getSortOrder(), req.getStatus());
        return ApiResponse.ok("updated", "success");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "管理员删除分类")
    public ApiResponse<String> delete(@PathVariable Long id) {
        Long adminId = AdminGuard.requireAdminUserId();
        categoryService.adminDelete(adminId, id);
        return ApiResponse.ok("deleted", "success");
    }
}
