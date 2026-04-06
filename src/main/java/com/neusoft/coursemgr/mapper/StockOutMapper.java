package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.StockOut;
import com.neusoft.coursemgr.domain.StockOutSummaryVO;
import com.neusoft.coursemgr.domain.StockOutVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StockOutMapper {

    int insert(StockOut stockOut);

    List<StockOutVO> selectList(@Param("drugId") Integer drugId,
                                @Param("outType") Integer outType,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    long countList(@Param("drugId") Integer drugId,
                   @Param("outType") Integer outType,
                   @Param("startTime") LocalDateTime startTime,
                   @Param("endTime") LocalDateTime endTime);

    StockOutSummaryVO sumByDateRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
}
