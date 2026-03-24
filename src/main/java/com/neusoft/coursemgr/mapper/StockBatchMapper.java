package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.StockBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockBatchMapper {

    int insert(StockBatch batch);

    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    StockBatch selectById(@Param("id") Integer id);

    List<StockBatch> selectByDrugId(@Param("drugId") Integer drugId);

    /** 查找同一药品相同批号的批次（用于入库时复用批次） */
    StockBatch selectByDrugIdAndBatchNo(@Param("drugId") Integer drugId,
                                        @Param("batchNo") String batchNo);

    List<StockBatch> selectExpiringSoon(@Param("days") Integer days);

    List<StockBatch> selectLowStock();
}
