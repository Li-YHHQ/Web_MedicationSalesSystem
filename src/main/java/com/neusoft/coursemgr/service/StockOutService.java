package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.StockOutRequest;
import com.neusoft.coursemgr.domain.StockOutVO;

public interface StockOutService {

    void stockOut(StockOutRequest req);

    PageResult<StockOutVO> listStockOut(Integer drugId, Integer outType,
                                        String startDate, String endDate,
                                        int page, int size);
}
