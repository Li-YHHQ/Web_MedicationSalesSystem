package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateSupplierRequest;
import com.neusoft.coursemgr.domain.Supplier;
import com.neusoft.coursemgr.domain.UpdateSupplierRequest;
import com.neusoft.coursemgr.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "供应商管理")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "分页查询供应商列表")
    public ApiResponse<PageResult<Supplier>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;
        return ApiResponse.ok(supplierService.list(keyword, status, page, size));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部正常状态的供应商（用于下拉选择）")
    public ApiResponse<List<Supplier>> listAll() {
        return ApiResponse.ok(supplierService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单个供应商")
    public ApiResponse<Supplier> getById(@PathVariable Integer id) {
        return ApiResponse.ok(supplierService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增供应商")
    public ApiResponse<String> create(@Valid @RequestBody CreateSupplierRequest req) {
        supplierService.create(req);
        return ApiResponse.ok("created", "success");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新供应商")
    public ApiResponse<String> update(@PathVariable Integer id,
                                      @Valid @RequestBody UpdateSupplierRequest req) {
        supplierService.update(id, req);
        return ApiResponse.ok("updated", "success");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除供应商（软删除）")
    public ApiResponse<String> delete(@PathVariable Integer id) {
        supplierService.delete(id);
        return ApiResponse.ok("deleted", "success");
    }
}
