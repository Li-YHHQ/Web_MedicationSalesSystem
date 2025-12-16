package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    int insert(User user);

    int updateProfile(@Param("id") Long id,
                      @Param("phone") String phone,
                      @Param("email") String email,
                      @Param("realName") String realName,
                      @Param("avatarUrl") String avatarUrl);

    List<User> selectUsers(@Param("keyword") String keyword);
}
