package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockOutSummaryVO {
    private BigDecimal totalSalesAmount;
    private BigDecimal totalCostAmount;
    private BigDecimal totalProfit;
}
