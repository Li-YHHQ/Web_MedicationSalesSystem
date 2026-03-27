package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateDrugRequest;
import com.neusoft.coursemgr.domain.Drug;
import com.neusoft.coursemgr.domain.StockBatch;
import com.neusoft.coursemgr.domain.UpdateDrugRequest;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.DrugMapper;
import com.neusoft.coursemgr.mapper.StockBatchMapper;
import com.neusoft.coursemgr.service.DrugService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DrugServiceImpl implements DrugService {

    private static final Logger log = LoggerFactory.getLogger(DrugServiceImpl.class);

    private final DrugMapper drugMapper;
    private final StockBatchMapper stockBatchMapper;

    public DrugServiceImpl(DrugMapper drugMapper, StockBatchMapper stockBatchMapper) {
        this.drugMapper = drugMapper;
        this.stockBatchMapper = stockBatchMapper;
    }

    @Override
    public PageResult<Drug> list(String keyword, String category, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        List<Drug> list = drugMapper.selectList(keyword, category, status, offset, size);
        long total = drugMapper.countList(keyword, category, status);
        return new PageResult<>(total, list);
    }

    @Override
    public Drug getById(Integer id) {
        Drug drug = drugMapper.selectById(id);
        if (drug == null) {
            throw new BizException(404, "药品不存在");
        }
        return drug;
    }

    @Override
    public void create(CreateDrugRequest req) {
        Drug existing = drugMapper.selectByDrugCode(req.getDrugCode());
        if (existing != null) {
            throw new BizException(400, "药品编码已存在");
        }

        Drug drug = new Drug();
        drug.setDrugCode(req.getDrugCode());
        drug.setDrugName(req.getDrugName());
        drug.setCommonName(req.getCommonName());
        drug.setCategory(req.getCategory());
        drug.setUnit(req.getUnit());
        drug.setSpec(req.getSpec());
        drug.setManufacturer(req.getManufacturer());
        drug.setApprovalNo(req.getApprovalNo());
        drug.setBarcode(req.getBarcode());
        drug.setCostPrice(req.getCostPrice());
        drug.setRetailPrice(req.getRetailPrice());
        drug.setStockMin(req.getStockMin() != null ? req.getStockMin() : 0);
        drug.setStatus(1);

        drugMapper.insert(drug);
        log.info("drug created, id={}, drugCode={}", drug.getId(), drug.getDrugCode());
    }

    @Override
    public void update(Integer id, UpdateDrugRequest req) {
        Drug existing = drugMapper.selectById(id);
        if (existing == null) {
            throw new BizException(404, "药品不存在");
        }

        Drug drug = new Drug();
        drug.setId(id);
        drug.setDrugName(req.getDrugName());
        drug.setCommonName(req.getCommonName());
        drug.setCategory(req.getCategory());
        drug.setUnit(req.getUnit());
        drug.setSpec(req.getSpec());
        drug.setManufacturer(req.getManufacturer());
        drug.setApprovalNo(req.getApprovalNo());
        drug.setBarcode(req.getBarcode());
        drug.setCostPrice(req.getCostPrice());
        drug.setRetailPrice(req.getRetailPrice());
        drug.setStockMin(req.getStockMin());
        drug.setStatus(req.getStatus());

        drugMapper.updateById(drug);
        log.info("drug updated, id={}", id);
    }

    @Override
    public void delete(Integer id) {
        Drug existing = drugMapper.selectById(id);
        if (existing == null) {
            throw new BizException(404, "药品不存在");
        }
        drugMapper.deleteById(id);
        log.info("drug deleted (soft), id={}", id);
    }

    @Override
    public List<Drug> listAll(String keyword, String category, Integer status) {
        return drugMapper.selectList(keyword, category, status, null, null);
    }

    // -------------------------------------------------------------------------
    // 格式一：药品档案表导入
    // -------------------------------------------------------------------------
    @Override
    public void importDrugs(Map<String, Integer> nameToIdx, List<Map<Integer, String>> rows) {
        int imported = 0, updated = 0, skipped = 0;
        for (Map<Integer, String> row : rows) {
            String drugCode = str(row, nameToIdx, "药品编码");
            String drugName = str(row, nameToIdx, "药品名称");
            if (isBlank(drugCode) || isBlank(drugName)) {
                skipped++;
                continue;
            }

            Drug existing = drugMapper.selectByDrugCode(drugCode);
            Drug drug;
            if (existing != null) {
                // 更新已有药品（updateById 使用 <if test="field != null"> 跳过 null 字段）
                drug = new Drug();
                drug.setId(existing.getId());
                drug.setDrugName(drugName);
                drug.setCommonName(str(row, nameToIdx, "通用名称"));
                drug.setCategory(str(row, nameToIdx, "类别"));
                drug.setUnit(str(row, nameToIdx, "单位"));
                drug.setSpec(str(row, nameToIdx, "规格"));
                drug.setManufacturer(str(row, nameToIdx, "生产厂家"));
                drug.setApprovalNo(str(row, nameToIdx, "批准文号"));
                drug.setBarcode(str(row, nameToIdx, "条码"));
                drug.setCostPrice(decimal(row, nameToIdx, "成本价"));
                drug.setRetailPrice(decimal(row, nameToIdx, "零售价"));
                drug.setStockMin(integer(row, nameToIdx, "库存下限"));
                drugMapper.updateById(drug);
                updated++;
            } else {
                // 新建药品
                drug = new Drug();
                drug.setDrugCode(drugCode);
                drug.setDrugName(drugName);
                drug.setCommonName(str(row, nameToIdx, "通用名称"));
                drug.setCategory(str(row, nameToIdx, "类别"));
                drug.setUnit(str(row, nameToIdx, "单位"));
                drug.setSpec(str(row, nameToIdx, "规格"));
                drug.setManufacturer(str(row, nameToIdx, "生产厂家"));
                drug.setApprovalNo(str(row, nameToIdx, "批准文号"));
                drug.setBarcode(str(row, nameToIdx, "条码"));
                drug.setCostPrice(decimal(row, nameToIdx, "成本价"));
                drug.setRetailPrice(decimal(row, nameToIdx, "零售价"));
                Integer stockMin = integer(row, nameToIdx, "库存下限");
                drug.setStockMin(stockMin != null ? stockMin : 0);
                drug.setStatus(1);
                drugMapper.insert(drug);
                imported++;
            }

            // 如果有"药品库存"列且值 > 0，创建初始批次
            Integer initQty = integer(row, nameToIdx, "药品库存");
            if (initQty != null && initQty > 0) {
                StockBatch batch = new StockBatch();
                batch.setDrugId(drug.getId());
                batch.setBatchNo("初始批次");
                batch.setQuantity(initQty);
                batch.setCostPrice(decimal(row, nameToIdx, "成本价"));
                batch.setExpireDate(LocalDate.of(2099, 12, 31));
                batch.setStockInDate(LocalDate.now());
                batch.setStatus(1);
                stockBatchMapper.insert(batch);
            }
        }
        log.info("importDrugs: imported={}, updated={}, skipped={}", imported, updated, skipped);
    }

    // -------------------------------------------------------------------------
    // 格式二：效期批次表导入
    // -------------------------------------------------------------------------
    @Override
    public void importBatches(Map<String, Integer> nameToIdx, List<Map<Integer, String>> rows) {
        int updated = 0, inserted = 0, skipped = 0;
        for (Map<Integer, String> row : rows) {
            String drugCode = str(row, nameToIdx, "药品编码");
            if (isBlank(drugCode)) {
                skipped++;
                continue;
            }

            Drug drug = drugMapper.selectByDrugCode(drugCode);
            if (drug == null) {
                skipped++;
                continue;
            }

            LocalDate expireDate  = parseDate(str(row, nameToIdx, "有效期"));
            LocalDate produceDate = parseDate(str(row, nameToIdx, "生产日期"));
            LocalDate stockInDate = parseDate(str(row, nameToIdx, "入库日期"));
            String    batchNo     = str(row, nameToIdx, "生产批号");

            // 查找该药品的"初始批次"
            StockBatch existing = stockBatchMapper.selectByDrugIdAndBatchNo(drug.getId(), "初始批次");
            if (existing != null) {
                // 回填效期及相关字段
                existing.setExpireDate(expireDate);
                existing.setProduceDate(produceDate);
                if (!isBlank(batchNo)) {
                    existing.setBatchNo(batchNo);
                }
                if (stockInDate != null) {
                    existing.setStockInDate(stockInDate);
                }
                stockBatchMapper.updateBatchInfo(existing);
                updated++;
            } else {
                // 没有初始批次，直接新建
                StockBatch batch = new StockBatch();
                batch.setDrugId(drug.getId());
                batch.setBatchNo(!isBlank(batchNo) ? batchNo : "批次导入");
                batch.setExpireDate(expireDate);
                batch.setProduceDate(produceDate);
                batch.setStockInDate(stockInDate != null ? stockInDate : LocalDate.now());
                Integer qty = integer(row, nameToIdx, "库存数量");
                batch.setQuantity(qty != null ? qty : 0);
                batch.setStatus(1);
                stockBatchMapper.insert(batch);
                inserted++;
            }
        }
        log.info("importBatches: updated={}, inserted={}, skipped={}", updated, inserted, skipped);
    }

    // -------------------------------------------------------------------------
    // 工具方法
    // -------------------------------------------------------------------------

    /** 按列名从行数据中取字符串，trim 后返回；列不存在或为空返回 null */
    private static String str(Map<Integer, String> row, Map<String, Integer> nameToIdx, String colName) {
        Integer idx = nameToIdx.get(colName);
        if (idx == null) return null;
        String val = row.get(idx);
        if (val == null) return null;
        String trimmed = val.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static BigDecimal decimal(Map<Integer, String> row, Map<String, Integer> nameToIdx, String colName) {
        String val = str(row, nameToIdx, colName);
        if (val == null) return null;
        try { return new BigDecimal(val); } catch (NumberFormatException e) { return null; }
    }

    private static Integer integer(Map<Integer, String> row, Map<String, Integer> nameToIdx, String colName) {
        String val = str(row, nameToIdx, colName);
        if (val == null) return null;
        try {
            // 兼容 Excel 数字可能带小数点（如 "10.0"）
            return (int) Double.parseDouble(val);
        } catch (NumberFormatException e) { return null; }
    }

    /**
     * 解析日期字符串：兼容 "yyyy-MM-dd" 文本格式和 Excel 数字日期（从 1900-01-00 起的天数）。
     */
    static LocalDate parseDate(String value) {
        if (isBlank(value)) return null;
        String v = value.trim();
        // 尝试标准字符串格式
        try { return LocalDate.parse(v); } catch (Exception ignored) {}
        // 尝试 Excel 数字日期（基准：1899-12-30）
        try {
            long days = (long) Double.parseDouble(v);
            return LocalDate.of(1899, 12, 30).plusDays(days);
        } catch (Exception ignored) {}
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
