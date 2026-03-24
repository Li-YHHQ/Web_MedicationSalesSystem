package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DrugMapper {

    int insert(Drug drug);

    int updateById(Drug drug);

    int deleteById(@Param("id") Integer id);

    Drug selectById(@Param("id") Integer id);

    List<Drug> selectList(@Param("keyword") String keyword,
                          @Param("category") String category,
                          @Param("status") Integer status,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);

    long countList(@Param("keyword") String keyword,
                   @Param("category") String category,
                   @Param("status") Integer status);

    Drug selectByDrugCode(@Param("drugCode") String drugCode);
}
