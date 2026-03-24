package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.DashboardVO;
import com.neusoft.coursemgr.domain.FinanceDaily;
import com.neusoft.coursemgr.domain.StockOutSummaryVO;
import com.neusoft.coursemgr.mapper.DashboardMapper;
import com.neusoft.coursemgr.mapper.FinanceDailyMapper;
import com.neusoft.coursemgr.mapper.StockBatchMapper;
import com.neusoft.coursemgr.mapper.StockOutMapper;
import com.neusoft.coursemgr.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;
    private final StockBatchMapper stockBatchMapper;
    private final StockOutMapper stockOutMapper;
    private final FinanceDailyMapper financeDailyMapper;

    public DashboardServiceImpl(DashboardMapper dashboardMapper,
                                StockBatchMapper stockBatchMapper,
                                StockOutMapper stockOutMapper,
                                FinanceDailyMapper financeDailyMapper) {
        this.dashboardMapper = dashboardMapper;
        this.stockBatchMapper = stockBatchMapper;
        this.stockOutMapper = stockOutMapper;
        this.financeDailyMapper = financeDailyMapper;
    }

    @Override
    public DashboardVO getDashboard() {
        LocalDate today = LocalDate.now();
        String todayStr      = today.toString();
        String monthStartStr = today.withDayOfMonth(1).toString();
        String sevenDaysAgo  = today.minusDays(6).toString();

        // 药品总数
        int totalDrugCount = dashboardMapper.countTotalDrugs();

        // 总库存数量
        long totalStock = dashboardMapper.sumTotalStock();

        // 即将过期批次数（90天内）
        int expiringSoonCount = stockBatchMapper.selectExpiringSoon(90).size();

        // 库存不足批次数
        int lowStockCount = stockBatchMapper.selectLowStock().size();

        // 今日销售额 / 利润
        StockOutSummaryVO todayStats = stockOutMapper.sumByDateRange(todayStr, todayStr);
        BigDecimal todaySalesAmount  = valueOf(todayStats.getTotalSalesAmount());
        BigDecimal todayProfitAmount = valueOf(todayStats.getTotalProfit());

        // 本月销售额 / 利润
        StockOutSummaryVO monthStats = stockOutMapper.sumByDateRange(monthStartStr, todayStr);
        BigDecimal monthSalesAmount  = valueOf(monthStats.getTotalSalesAmount());
        BigDecimal monthProfitAmount = valueOf(monthStats.getTotalProfit());

        // 最近7天每日财务数据
        List<FinanceDaily> recentFinanceList = financeDailyMapper.selectList(sevenDaysAgo, todayStr);

        DashboardVO vo = new DashboardVO();
        vo.setTotalDrugCount(totalDrugCount);
        vo.setTotalStock((int) totalStock);
        vo.setExpiringSoonCount(expiringSoonCount);
        vo.setLowStockCount(lowStockCount);
        vo.setTodaySalesAmount(todaySalesAmount);
        vo.setTodayProfitAmount(todayProfitAmount);
        vo.setMonthSalesAmount(monthSalesAmount);
        vo.setMonthProfitAmount(monthProfitAmount);
        vo.setRecentFinanceList(recentFinanceList);
        return vo;
    }

    private static BigDecimal valueOf(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
