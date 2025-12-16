package com.neusoft.coursemgr.auth;

import com.neusoft.coursemgr.exception.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }

        String token = header.substring("Bearer ".length()).trim();
        try {
            Claims claims = jwtUtil.parse(token).getPayload();
            Long uid = claims.get("uid", Long.class);
            String role = claims.get("role", String.class);
            if (uid == null || role == null) {
                throw new BizException(401, "无效token");
            }
            AuthContext.set(new AuthContext.AuthUser(uid, role));
            return true;
        } catch (JwtException e) {
            throw new BizException(401, "token无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
