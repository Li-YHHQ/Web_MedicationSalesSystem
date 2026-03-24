package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplierMapper {

    int insert(Supplier supplier);

    int updateById(Supplier supplier);

    int deleteById(@Param("id") Integer id);

    Supplier selectById(@Param("id") Integer id);

    List<Supplier> selectList(@Param("keyword") String keyword,
                              @Param("status") Integer status,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    long countList(@Param("keyword") String keyword,
                   @Param("status") Integer status);
}
