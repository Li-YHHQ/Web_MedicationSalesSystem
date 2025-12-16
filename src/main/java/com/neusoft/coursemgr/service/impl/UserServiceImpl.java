package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.auth.JwtUtil;
import com.neusoft.coursemgr.domain.LoginResponse;
import com.neusoft.coursemgr.domain.UpdateProfileRequest;
import com.neusoft.coursemgr.domain.User;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.UserMapper;
import com.neusoft.coursemgr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Long register(String username, String password) {
        User exist = userMapper.selectByUsername(username);
        if (exist != null) {
            throw new BizException(400, "用户名已存在");
        }

        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(password));
        u.setRole("USER");
        u.setStatus(1);
        userMapper.insert(u);
        log.info("user register success, userId={}, username={}", u.getId(), username);
        return u.getId();
    }

    @Override
    public LoginResponse login(String username, String password) {
        User u = userMapper.selectByUsername(username);
        if (u == null || u.getStatus() == null || u.getStatus() != 1) {
            log.info("login failed, username={}", username);
            throw new BizException(400, "用户名或密码错误");
        }

        boolean ok = encoder.matches(password, u.getPasswordHash());
        if (!ok) {
            log.info("login failed, username={}", username);
            throw new BizException(400, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(u.getId(), u.getRole());
        log.info("login success, userId={}, username={}, role={}", u.getId(), username, u.getRole());
        return new LoginResponse(token, u.getRole());
    }

    @Override
    public User getMe(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new BizException(404, "用户不存在");
        }
        u.setPasswordHash(null);
        return u;
    }

    @Override
    public void updateMe(Long userId, UpdateProfileRequest req) {
        int rows = userMapper.updateProfile(userId, req.getPhone(), req.getEmail(), req.getRealName(), req.getAvatarUrl());
        if (rows <= 0) {
            throw new BizException(404, "用户不存在");
        }
        log.info("user update profile, userId={}", userId);
    }

    @Override
    public List<User> adminListUsers(Long adminUserId, String keyword) {
        User admin = userMapper.selectById(adminUserId);
        if (admin == null) {
            throw new BizException(404, "用户不存在");
        }
        if (!"ADMIN".equals(admin.getRole())) {
            throw new BizException(403, "无权限");
        }
        List<User> list = userMapper.selectUsers(keyword);
        for (User u : list) {
            u.setPasswordHash(null);
        }
        return list;
    }
}
