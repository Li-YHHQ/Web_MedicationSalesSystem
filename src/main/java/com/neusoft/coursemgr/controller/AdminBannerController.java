package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.Banner;
import com.neusoft.coursemgr.domain.CreateBannerRequest;
import com.neusoft.coursemgr.domain.UpdateBannerRequest;
import com.neusoft.coursemgr.domain.UpdateBannerStatusRequest;
import com.neusoft.coursemgr.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
@Tag(name = "管理员-轮播图管理")
public class AdminBannerController {

    private final BannerService bannerService;

    public AdminBannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    @Operation(summary = "管理员轮播图列表")
    public ApiResponse<List<Banner>> listAll() {
        Long adminId = AdminGuard.requireAdminUserId();
        return ApiResponse.ok(bannerService.adminListAll(adminId));
    }

    @PostMapping
    @Operation(summary = "管理员新增轮播图")
    public ApiResponse<Long> create(@Valid @RequestBody CreateBannerRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        Banner b = new Banner();
        b.setTitle(req.getTitle());
        b.setImageUrl(req.getImageUrl());
        b.setLinkUrl(req.getLinkUrl());
        b.setSortOrder(req.getSortOrder());
        return ApiResponse.ok("created", bannerService.adminCreate(adminId, b));
    }

    @PutMapping("/{id}")
    @Operation(summary = "管理员更新轮播图")
    public ApiResponse<String> update(@PathVariable Long id, @Valid @RequestBody UpdateBannerRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        Banner b = new Banner();
        b.setId(id);
        b.setTitle(req.getTitle());
        b.setImageUrl(req.getImageUrl());
        b.setLinkUrl(req.getLinkUrl());
        b.setSortOrder(req.getSortOrder());
        b.setStatus(req.getStatus());
        bannerService.adminUpdate(adminId, b);
        return ApiResponse.ok("updated", "success");
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "管理员启用/停用轮播图")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateBannerStatusRequest req) {
        Long adminId = AdminGuard.requireAdminUserId();
        bannerService.adminUpdateStatus(adminId, id, req.getStatus());
        return ApiResponse.ok("updated", "success");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "管理员删除轮播图")
    public ApiResponse<String> delete(@PathVariable Long id) {
        Long adminId = AdminGuard.requireAdminUserId();
        bannerService.adminDelete(adminId, id);
        return ApiResponse.ok("deleted", "success");
    }
}
