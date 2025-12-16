package com.neusoft.coursemgr.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CartMapper {

    Long selectCartIdByUserId(@Param("userId") Long userId);

    int insertCart(@Param("userId") Long userId);
}
