package com.neusoft.coursemgr.domain;

import lombok.Data;

@Data
public class StockSyncRequest {
    /** 同步日期，格式 yyyy-MM-dd */
    private String syncDate;
}
