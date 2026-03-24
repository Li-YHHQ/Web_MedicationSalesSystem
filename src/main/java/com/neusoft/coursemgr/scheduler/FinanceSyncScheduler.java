package com.neusoft.coursemgr.scheduler;

import com.neusoft.coursemgr.service.FinanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FinanceSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(FinanceSyncScheduler.class);

    private final FinanceService financeService;

    public FinanceSyncScheduler(FinanceService financeService) {
        this.financeService = financeService;
    }

    /**
     * 每天凌晨 1 点自动同步昨日财务数据。
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduledSyncToday() {
        log.info("finance scheduled sync start");
        try {
            financeService.syncToday();
            log.info("finance scheduled sync success");
        } catch (Exception e) {
            log.error("finance scheduled sync failed", e);
        }
    }
}
