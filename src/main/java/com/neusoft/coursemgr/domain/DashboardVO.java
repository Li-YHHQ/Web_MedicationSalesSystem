package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardVO {
    private Integer totalDrugCount;
    private Integer totalStock;
    private Integer expiringSoonCount;
    private Integer lowStockCount;
    private BigDecimal todaySalesAmount;
    private BigDecimal todayProfitAmount;
    private BigDecimal monthSalesAmount;
    private BigDecimal monthProfitAmount;
    private List<FinanceDaily> recentFinanceList;
}
