package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.FinanceSummaryVO;

import java.time.LocalDate;

public interface FinanceService {

    void syncDaily(LocalDate date);

    FinanceSummaryVO getSummary(String startDate, String endDate);

    void syncToday();
}
