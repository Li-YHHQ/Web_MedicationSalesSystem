package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Category selectById(@Param("id") Long id);

    Category selectByName(@Param("name") String name);

    List<Category> selectEnabled();

    List<Category> selectAll();

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Long id);
}
