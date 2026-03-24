package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.StockOut;
import com.neusoft.coursemgr.domain.StockOutSummaryVO;
import com.neusoft.coursemgr.domain.StockOutVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockOutMapper {

    int insert(StockOut stockOut);

    List<StockOutVO> selectList(@Param("drugId") Integer drugId,
                                @Param("outType") Integer outType,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    long countList(@Param("drugId") Integer drugId,
                   @Param("outType") Integer outType,
                   @Param("startDate") String startDate,
                   @Param("endDate") String endDate);

    StockOutSummaryVO sumByDateRange(@Param("startDate") String startDate,
                                     @Param("endDate") String endDate);
}
