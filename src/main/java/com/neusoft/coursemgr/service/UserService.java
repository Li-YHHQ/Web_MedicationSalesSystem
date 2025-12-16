package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.LoginResponse;
import com.neusoft.coursemgr.domain.UpdateProfileRequest;
import com.neusoft.coursemgr.domain.User;

import java.util.List;

public interface UserService {

    Long register(String username, String password);

    LoginResponse login(String username, String password);

    User getMe(Long userId);

    void updateMe(Long userId, UpdateProfileRequest req);

    List<User> adminListUsers(Long adminUserId, String keyword);
}
