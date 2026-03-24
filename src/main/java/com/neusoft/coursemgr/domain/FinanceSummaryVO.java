package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FinanceSummaryVO {
    private BigDecimal totalSalesAmount;
    private BigDecimal totalCostAmount;
    private BigDecimal totalProfitAmount;
    private Integer totalSalesCount;
    private List<FinanceDaily> dailyList;
}
