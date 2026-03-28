package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.util.List;

@Data
public class StockSyncPreview {
    /** 新增药品数 */
    private int newDrugCount;
    /** 入库记录数 */
    private int inCount;
    /** 出库记录数 */
    private int outCount;
    /** 未变化数 */
    private int unchangedCount;
    /** 每条药品的变化详情 */
    private List<StockSyncDetail> details;
}
