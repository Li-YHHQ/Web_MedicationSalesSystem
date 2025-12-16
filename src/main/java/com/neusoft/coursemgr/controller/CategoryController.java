package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.Category;
import com.neusoft.coursemgr.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "分类（公开）")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "获取可用分类列表")
    public ApiResponse<List<Category>> list() {
        return ApiResponse.ok(categoryService.listEnabled());
    }
}
