package com.neusoft.coursemgr.auth;

import com.neusoft.coursemgr.exception.BizException;

public final class AdminGuard {

    private AdminGuard() {
    }

    public static Long requireAdminUserId() {
        AuthContext.AuthUser u = AuthContext.get();
        if (u == null) {
            throw new BizException(401, "未登录");
        }
        if (!"ADMIN".equals(u.role())) {
            throw new BizException(403, "无权限");
        }
        return u.userId();
    }

    public static Long requireLoginUserId() {
        AuthContext.AuthUser u = AuthContext.get();
        if (u == null) {
            throw new BizException(401, "未登录");
        }
        return u.userId();
    }
}
