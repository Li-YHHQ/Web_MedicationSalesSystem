package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.StockBatch;
import com.neusoft.coursemgr.domain.StockInRequest;
import com.neusoft.coursemgr.domain.StockInVO;

import java.util.List;

public interface StockInService {

    void stockIn(StockInRequest req);

    PageResult<StockInVO> listStockIn(Integer drugId, Integer supplierId,
                                      String startDate, String endDate,
                                      int page, int size);

    List<StockBatch> getBatchesByDrugId(Integer drugId);

    List<StockBatch> getExpiringSoon(int days);

    List<StockBatch> getLowStock();
}
