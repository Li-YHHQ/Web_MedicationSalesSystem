package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.StockIn;
import com.neusoft.coursemgr.domain.StockInVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockInMapper {

    int insert(StockIn stockIn);

    List<StockInVO> selectList(@Param("drugId") Integer drugId,
                               @Param("supplierId") Integer supplierId,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    long countList(@Param("drugId") Integer drugId,
                   @Param("supplierId") Integer supplierId,
                   @Param("startDate") String startDate,
                   @Param("endDate") String endDate);
}
