package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AuthContext;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.LoginResponse;
import com.neusoft.coursemgr.domain.UpdateProfileRequest;
import com.neusoft.coursemgr.domain.User;
import com.neusoft.coursemgr.domain.UserLoginRequest;
import com.neusoft.coursemgr.domain.UserRegisterRequest;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户模块")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<Long> register(@Valid @RequestBody UserRegisterRequest req) {
        return ApiResponse.ok("created", userService.register(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody UserLoginRequest req) {
        return ApiResponse.ok(userService.login(req.getUsername(), req.getPassword()));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<User> me() {
        AuthContext.AuthUser au = AuthContext.get();
        if (au == null) {
            throw new BizException(401, "未登录");
        }
        return ApiResponse.ok(userService.getMe(au.userId()));
    }

    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息")
    public ApiResponse<String> updateMe(@Valid @RequestBody UpdateProfileRequest req) {
        AuthContext.AuthUser au = AuthContext.get();
        if (au == null) {
            throw new BizException(401, "未登录");
        }
        userService.updateMe(au.userId(), req);
        return ApiResponse.ok("updated", "success");
    }
}
