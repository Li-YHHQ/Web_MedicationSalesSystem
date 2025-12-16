package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AuthContext;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.User;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "管理员-用户管理")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "管理员查询用户列表")
    public ApiResponse<List<User>> list(@RequestParam(required = false) String keyword) {
        AuthContext.AuthUser au = AuthContext.get();
        if (au == null) {
            throw new BizException(401, "未登录");
        }
        return ApiResponse.ok(userService.adminListUsers(au.userId(), keyword));
    }
}
