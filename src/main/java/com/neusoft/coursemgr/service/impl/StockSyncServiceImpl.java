package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.auth.AuthContext;
import com.neusoft.coursemgr.domain.*;
import com.neusoft.coursemgr.mapper.DrugMapper;
import com.neusoft.coursemgr.mapper.StockBatchMapper;
import com.neusoft.coursemgr.mapper.StockInMapper;
import com.neusoft.coursemgr.mapper.StockOutMapper;
import com.neusoft.coursemgr.service.StockSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class StockSyncServiceImpl implements StockSyncService {

    private static final Logger log = LoggerFactory.getLogger(StockSyncServiceImpl.class);

    private static final String BATCH_INIT = "初始批次";

    private final DrugMapper       drugMapper;
    private final StockBatchMapper stockBatchMapper;
    private final StockInMapper    stockInMapper;
    private final StockOutMapper   stockOutMapper;

    public StockSyncServiceImpl(DrugMapper drugMapper,
                                StockBatchMapper stockBatchMapper,
                                StockInMapper stockInMapper,
                                StockOutMapper stockOutMapper) {
        this.drugMapper       = drugMapper;
        this.stockBatchMapper = stockBatchMapper;
        this.stockInMapper    = stockInMapper;
        this.stockOutMapper   = stockOutMapper;
    }

    // -------------------------------------------------------------------------
    // preview — 只读，不写库
    // -------------------------------------------------------------------------

    @Override
    public StockSyncPreview preview(Map<String, Integer> drugStockMap, String syncDate) {
        List<StockSyncDetail> details = new ArrayList<>();
        int newCount = 0, inCount = 0, outCount = 0, unchangedCount = 0;

        for (Map.Entry<String, Integer> entry : drugStockMap.entrySet()) {
            StockSyncDetail d = analyze(entry.getKey(), entry.getValue());
            details.add(d);
            switch (d.getChangeType()) {
                case "NEW"       -> newCount++;
                case "IN"        -> inCount++;
                case "OUT"       -> outCount++;
                case "UNCHANGED" -> unchangedCount++;
            }
        }

        StockSyncPreview preview = new StockSyncPreview();
        preview.setNewDrugCount(newCount);
        preview.setInCount(inCount);
        preview.setOutCount(outCount);
        preview.setUnchangedCount(unchangedCount);
        preview.setDetails(details);
        return preview;
    }

    // -------------------------------------------------------------------------
    // confirm — 事务写库（接收完整行数据，NEW 分支可写入完整药品档案）
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void confirm(Map<String, Map<String, String>> fullDataMap, String syncDate) {
        int operatorId = currentOperatorId();
        LocalDate date = LocalDate.parse(syncDate);

        for (Map.Entry<String, Map<String, String>> entry : fullDataMap.entrySet()) {
            String drugCode = entry.getKey();
            Map<String, String> rowData = entry.getValue();
            int newQty = parseInt(rowData.getOrDefault("药品库存", "0"));

            StockSyncDetail d = analyze(drugCode, newQty);

            switch (d.getChangeType()) {
                case "NEW"  -> handleNew(d, rowData, date, operatorId);
                case "IN"   -> handleIn(d, date, operatorId);
                case "OUT"  -> handleOut(d, date, operatorId);
                // UNCHANGED: 跳过
            }
        }
        log.info("stock sync confirmed, syncDate={}, entries={}", syncDate, fullDataMap.size());
    }

    // -------------------------------------------------------------------------
    // 核心比对逻辑（preview 和 confirm 共用，均为只读）
    // -------------------------------------------------------------------------

    private StockSyncDetail analyze(String drugCode, int newQty) {
        StockSyncDetail d = new StockSyncDetail();
        d.setDrugCode(drugCode);
        d.setNewQuantity(newQty);

        Drug drug = drugMapper.selectByDrugCode(drugCode);
        if (drug == null) {
            d.setDrugName(drugCode);   // 新药品尚无名称，用编码占位
            d.setChangeType("NEW");
            d.setOldQuantity(0);
            d.setDiff(newQty);
            return d;
        }

        d.setDrugName(drug.getDrugName());

        // 累加该药品所有有效批次的库存
        List<StockBatch> batches = stockBatchMapper.selectByDrugId(drug.getId());
        int oldQty = batches.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 1)
                .mapToInt(b -> b.getQuantity() != null ? b.getQuantity() : 0)
                .sum();
        d.setOldQuantity(oldQty);

        if (newQty > oldQty) {
            d.setChangeType("IN");
            d.setDiff(newQty - oldQty);
        } else if (newQty < oldQty) {
            d.setChangeType("OUT");
            d.setDiff(oldQty - newQty);
        } else {
            d.setChangeType("UNCHANGED");
            d.setDiff(0);
        }
        return d;
    }

    // -------------------------------------------------------------------------
    // confirm 子处理
    // -------------------------------------------------------------------------

    /**
     * NEW：从完整行数据构建药品档案，再创建初始批次。
     * 字段缺失时降级处理（drugName 用 drugCode 占位，数字字段默认 0）。
     */
    private void handleNew(StockSyncDetail d, Map<String, String> rowData,
                           LocalDate date, int operatorId) {
        Drug drug = new Drug();
        drug.setDrugCode(d.getDrugCode());

        String drugName = nullIfBlank(rowData.getOrDefault("药品名称", ""));
        drug.setDrugName(drugName != null ? drugName : d.getDrugCode());
        drug.setCommonName(nullIfBlank(rowData.getOrDefault("通用名称", "")));
        drug.setCategory(nullIfBlank(rowData.getOrDefault("类别", "")));
        drug.setUnit(nullIfBlank(rowData.getOrDefault("单位", "")));
        drug.setSpec(nullIfBlank(rowData.getOrDefault("规格", "")));
        drug.setManufacturer(nullIfBlank(rowData.getOrDefault("生产厂家", "")));
        drug.setApprovalNo(nullIfBlank(rowData.getOrDefault("批准文号", "")));
        drug.setBarcode(nullIfBlank(rowData.getOrDefault("条码", "")));
        drug.setCostPrice(parseBigDecimal(rowData.getOrDefault("成本价", "")));
        drug.setRetailPrice(parseBigDecimal(rowData.getOrDefault("零售价", "")));
        drug.setStockMin(5);
        drug.setStatus(1);
        drugMapper.insert(drug);

        StockBatch batch = new StockBatch();
        batch.setDrugId(drug.getId());
        batch.setBatchNo(BATCH_INIT);
        batch.setQuantity(d.getNewQuantity());
        batch.setCostPrice(drug.getCostPrice());
        batch.setExpireDate(LocalDate.of(2099, 12, 31));
        batch.setStockInDate(date);
        batch.setStatus(1);
        stockBatchMapper.insert(batch);

        log.debug("sync NEW: drugCode={}, drugName={}, qty={}",
                d.getDrugCode(), drug.getDrugName(), d.getNewQuantity());
    }

    /** IN：找初始批次累加库存，写入入库流水 */
    private void handleIn(StockSyncDetail d, LocalDate date, int operatorId) {
        Drug drug = drugMapper.selectByDrugCode(d.getDrugCode());
        if (drug == null) return;

        // 优先使用初始批次；若不存在则取库存最多的有效批次
        StockBatch batch = stockBatchMapper.selectByDrugIdAndBatchNo(drug.getId(), BATCH_INIT);
        if (batch == null) {
            batch = stockBatchMapper.selectByDrugId(drug.getId()).stream()
                    .filter(b -> b.getStatus() != null && b.getStatus() == 1)
                    .max(Comparator.comparingInt(b -> b.getQuantity() != null ? b.getQuantity() : 0))
                    .orElse(null);
        }
        if (batch == null) {
            // 批次不存在，新建一条
            batch = new StockBatch();
            batch.setDrugId(drug.getId());
            batch.setBatchNo(BATCH_INIT);
            batch.setQuantity(d.getDiff());
            batch.setExpireDate(LocalDate.of(2099, 12, 31));
            batch.setStockInDate(date);
            batch.setStatus(1);
            stockBatchMapper.insert(batch);
        } else {
            int updated = (batch.getQuantity() != null ? batch.getQuantity() : 0) + d.getDiff();
            stockBatchMapper.updateQuantity(batch.getId(), updated);
        }

        StockIn stockIn = new StockIn();
        stockIn.setDrugId(drug.getId());
        stockIn.setBatchId(batch.getId());
        stockIn.setQuantity(d.getDiff());
        stockIn.setCostPrice(batch.getCostPrice());
        stockIn.setTotalAmount(calcAmount(batch.getCostPrice(), d.getDiff()));
        stockIn.setOperatorId(operatorId);
        stockIn.setRemark("库存同步入库");
        stockInMapper.insert(stockIn);

        log.debug("sync IN: drugCode={}, diff={}", d.getDrugCode(), d.getDiff());
    }

    /** OUT：找库存最多的批次扣减，写入出库流水 */
    private void handleOut(StockSyncDetail d, LocalDate date, int operatorId) {
        Drug drug = drugMapper.selectByDrugCode(d.getDrugCode());
        if (drug == null) return;

        // 选库存最多的有效批次扣减
        StockBatch batch = stockBatchMapper.selectByDrugId(drug.getId()).stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 1)
                .max(Comparator.comparingInt(b -> b.getQuantity() != null ? b.getQuantity() : 0))
                .orElse(null);
        if (batch == null) {
            log.warn("sync OUT skipped, no valid batch for drugCode={}", d.getDrugCode());
            return;
        }

        int remaining = (batch.getQuantity() != null ? batch.getQuantity() : 0) - d.getDiff();
        stockBatchMapper.updateQuantity(batch.getId(), Math.max(remaining, 0));

        StockOut stockOut = new StockOut();
        stockOut.setDrugId(drug.getId());
        stockOut.setBatchId(batch.getId());
        stockOut.setOutType(1);   // 1 = 销售
        stockOut.setQuantity(d.getDiff());
        stockOut.setCostPrice(batch.getCostPrice());
        stockOut.setTotalAmount(calcAmount(batch.getCostPrice(), d.getDiff()));
        stockOut.setOperatorId(operatorId);
        stockOut.setRemark("库存同步出库");
        stockOutMapper.insert(stockOut);

        log.debug("sync OUT: drugCode={}, diff={}", d.getDrugCode(), d.getDiff());
    }

    // -------------------------------------------------------------------------
    // 工具方法
    // -------------------------------------------------------------------------

    private static int currentOperatorId() {
        AuthContext.AuthUser user = AuthContext.get();
        return user != null ? user.userId().intValue() : 0;
    }

    private static BigDecimal calcAmount(BigDecimal unitPrice, int qty) {
        if (unitPrice == null) return null;
        return unitPrice.multiply(BigDecimal.valueOf(qty));
    }

    /** 解析整数，失败默认返回 0 */
    private static int parseInt(String val) {
        if (val == null || val.isBlank()) return 0;
        try { return (int) Double.parseDouble(val.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    /** 解析 BigDecimal，失败返回 null */
    private static BigDecimal parseBigDecimal(String val) {
        if (val == null || val.isBlank()) return null;
        try { return new BigDecimal(val.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    /** 空白字符串转 null，非空字符串 trim 后返回 */
    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
