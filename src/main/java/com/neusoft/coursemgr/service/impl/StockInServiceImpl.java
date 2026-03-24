package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.auth.AuthContext;
import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.StockBatch;
import com.neusoft.coursemgr.domain.StockIn;
import com.neusoft.coursemgr.domain.StockInRequest;
import com.neusoft.coursemgr.domain.StockInVO;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.StockBatchMapper;
import com.neusoft.coursemgr.mapper.StockInMapper;
import com.neusoft.coursemgr.service.StockInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockInServiceImpl implements StockInService {

    private static final Logger log = LoggerFactory.getLogger(StockInServiceImpl.class);

    private final StockBatchMapper stockBatchMapper;
    private final StockInMapper stockInMapper;

    public StockInServiceImpl(StockBatchMapper stockBatchMapper, StockInMapper stockInMapper) {
        this.stockBatchMapper = stockBatchMapper;
        this.stockInMapper = stockInMapper;
    }

    @Override
    @Transactional
    public void stockIn(StockInRequest req) {
        if (req.getExpireDate().isBefore(LocalDate.now())) {
            throw new BizException(400, "有效期不能早于当前日期");
        }

        // 查找或创建批次（相同 drug_id + batch_no 视为同一批次）
        StockBatch batch = null;
        if (req.getBatchNo() != null && !req.getBatchNo().isBlank()) {
            batch = stockBatchMapper.selectByDrugIdAndBatchNo(req.getDrugId(), req.getBatchNo());
        }

        if (batch == null) {
            // 新批次
            batch = new StockBatch();
            batch.setDrugId(req.getDrugId());
            batch.setBatchNo(req.getBatchNo());
            batch.setProduceDate(req.getProduceDate());
            batch.setExpireDate(req.getExpireDate());
            batch.setStockInDate(req.getStockInDate() != null ? req.getStockInDate() : LocalDate.now());
            batch.setQuantity(req.getQuantity());
            batch.setCostPrice(req.getCostPrice());
            batch.setSupplierId(req.getSupplierId());
            batch.setStatus(1);
            stockBatchMapper.insert(batch);
            log.info("stock batch created, batchId={}, drugId={}, batchNo={}",
                    batch.getId(), req.getDrugId(), req.getBatchNo());
        } else {
            // 复用已有批次，累加库存
            int newQuantity = batch.getQuantity() + req.getQuantity();
            stockBatchMapper.updateQuantity(batch.getId(), newQuantity);
            log.info("stock batch reused, batchId={}, addQty={}, newQty={}",
                    batch.getId(), req.getQuantity(), newQuantity);
        }

        // 记录入库单
        Long operatorId = AuthContext.require().userId();
        BigDecimal totalAmount = req.getCostPrice()
                .multiply(BigDecimal.valueOf(req.getQuantity()));

        StockIn stockIn = new StockIn();
        stockIn.setDrugId(req.getDrugId());
        stockIn.setBatchId(batch.getId());
        stockIn.setQuantity(req.getQuantity());
        stockIn.setCostPrice(req.getCostPrice());
        stockIn.setTotalAmount(totalAmount);
        stockIn.setSupplierId(req.getSupplierId());
        stockIn.setOperatorId(operatorId.intValue());
        stockIn.setRemark(req.getRemark());

        stockInMapper.insert(stockIn);
        log.info("stock in recorded, stockInId={}, drugId={}, qty={}, totalAmount={}",
                stockIn.getId(), req.getDrugId(), req.getQuantity(), totalAmount);
    }

    @Override
    public PageResult<StockInVO> listStockIn(Integer drugId, Integer supplierId,
                                             String startDate, String endDate,
                                             int page, int size) {
        int offset = (page - 1) * size;
        List<StockInVO> list = stockInMapper.selectList(drugId, supplierId, startDate, endDate, offset, size);
        long total = stockInMapper.countList(drugId, supplierId, startDate, endDate);
        return new PageResult<>(total, list);
    }

    @Override
    public List<StockBatch> getBatchesByDrugId(Integer drugId) {
        return stockBatchMapper.selectByDrugId(drugId);
    }

    @Override
    public List<StockBatch> getExpiringSoon(int days) {
        if (days < 0) {
            throw new BizException(400, "days 参数不能为负");
        }
        return stockBatchMapper.selectExpiringSoon(days);
    }

    @Override
    public List<StockBatch> getLowStock() {
        return stockBatchMapper.selectLowStock();
    }
}
