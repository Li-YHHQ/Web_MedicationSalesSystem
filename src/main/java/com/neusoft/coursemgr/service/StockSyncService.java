package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.StockSyncPreview;

import java.util.Map;

public interface StockSyncService {

    /**
     * 预览库存同步结果，不写入数据库。
     *
     * @param drugStockMap 药品编码 → 新库存数量
     * @param drugNameMap  药品编码 → 药品名称（来自 Excel，用于 NEW 类型展示真实名称）
     * @param syncDate     同步日期（yyyy-MM-dd），用作入/出库日期
     * @return 预览结果
     */
    StockSyncPreview preview(Map<String, Integer> drugStockMap,
                             Map<String, String> drugNameMap,
                             String syncDate);

    /**
     * 执行库存同步，写入数据库（事务保证原子性）。
     *
     * @param fullDataMap 药品编码 → 该行所有列名→值的 Map（完整 Excel 行数据）
     * @param syncDate    同步日期（yyyy-MM-dd）
     */
    void confirm(Map<String, Map<String, String>> fullDataMap, String syncDate);
}
