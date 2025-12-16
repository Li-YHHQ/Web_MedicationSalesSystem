package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BannerMapper {

    Banner selectById(@Param("id") Long id);

    List<Banner> selectEnabled();

    List<Banner> selectAll();

    int insert(Banner banner);

    int update(Banner banner);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}
