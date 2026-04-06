package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.FinanceDaily;
import com.neusoft.coursemgr.domain.FinanceSummaryVO;
import com.neusoft.coursemgr.domain.StockOutSummaryVO;
import com.neusoft.coursemgr.mapper.FinanceDailyMapper;
import com.neusoft.coursemgr.mapper.StockOutMapper;
import com.neusoft.coursemgr.service.FinanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceServiceImpl implements FinanceService {

    private static final Logger log = LoggerFactory.getLogger(FinanceServiceImpl.class);

    private final FinanceDailyMapper financeDailyMapper;
    private final StockOutMapper stockOutMapper;

    public FinanceServiceImpl(FinanceDailyMapper financeDailyMapper, StockOutMapper stockOutMapper) {
        this.financeDailyMapper = financeDailyMapper;
        this.stockOutMapper = stockOutMapper;
    }

    @Override
    @Transactional
    public void syncDaily(LocalDate date) {
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime   = date.plusDays(1).atStartOfDay();

        // 从 stock_out_table 汇总当天数据
        StockOutSummaryVO stats = stockOutMapper.sumByDateRange(startTime, endTime);

        BigDecimal salesAmount  = stats.getTotalSalesAmount()  != null ? stats.getTotalSalesAmount()  : BigDecimal.ZERO;
        BigDecimal costAmount   = stats.getTotalCostAmount()   != null ? stats.getTotalCostAmount()   : BigDecimal.ZERO;
        BigDecimal profitAmount = stats.getTotalProfit()       != null ? stats.getTotalProfit()       : BigDecimal.ZERO;
        int salesCount          = stats.getSalesCount()        != null ? stats.getSalesCount()        : 0;

        FinanceDaily financeDaily = new FinanceDaily();
        financeDaily.setStatDate(date);
        financeDaily.setSalesAmount(salesAmount);
        financeDaily.setCostAmount(costAmount);
        financeDaily.setProfitAmount(profitAmount);
        financeDaily.setSalesCount(salesCount);

        // INSERT 或 UPDATE（stat_date 为 UNIQUE）
        FinanceDaily existing = financeDailyMapper.selectByDate(date);
        if (existing == null) {
            financeDailyMapper.insert(financeDaily);
            log.info("finance daily inserted, date={}, sales={}, cost={}, profit={}, count={}",
                    date, salesAmount, costAmount, profitAmount, salesCount);
        } else {
            financeDailyMapper.updateByDate(financeDaily);
            log.info("finance daily updated, date={}, sales={}, cost={}, profit={}, count={}",
                    date, salesAmount, costAmount, profitAmount, salesCount);
        }
    }

    @Override
    public FinanceSummaryVO getSummary(String startDate, String endDate) {
        List<FinanceDaily> dailyList = financeDailyMapper.selectList(startDate, endDate);

        BigDecimal totalSales  = BigDecimal.ZERO;
        BigDecimal totalCost   = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        int totalCount = 0;

        for (FinanceDaily d : dailyList) {
            if (d.getSalesAmount()  != null) totalSales  = totalSales.add(d.getSalesAmount());
            if (d.getCostAmount()   != null) totalCost   = totalCost.add(d.getCostAmount());
            if (d.getProfitAmount() != null) totalProfit = totalProfit.add(d.getProfitAmount());
            if (d.getSalesCount()   != null) totalCount += d.getSalesCount();
        }

        FinanceSummaryVO vo = new FinanceSummaryVO();
        vo.setTotalSalesAmount(totalSales);
        vo.setTotalCostAmount(totalCost);
        vo.setTotalProfitAmount(totalProfit);
        vo.setTotalSalesCount(totalCount);
        vo.setDailyList(dailyList);
        return vo;
    }

    @Override
    public void syncToday() {
        syncDaily(LocalDate.now());
    }
}
