package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.auth.AuthContext;
import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.StockBatch;
import com.neusoft.coursemgr.domain.StockOut;
import com.neusoft.coursemgr.domain.StockOutRequest;
import com.neusoft.coursemgr.domain.StockOutVO;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.StockBatchMapper;
import com.neusoft.coursemgr.mapper.StockOutMapper;
import com.neusoft.coursemgr.service.StockOutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockOutServiceImpl implements StockOutService {

    private static final Logger log = LoggerFactory.getLogger(StockOutServiceImpl.class);

    private final StockOutMapper stockOutMapper;
    private final StockBatchMapper stockBatchMapper;

    public StockOutServiceImpl(StockOutMapper stockOutMapper, StockBatchMapper stockBatchMapper) {
        this.stockOutMapper = stockOutMapper;
        this.stockBatchMapper = stockBatchMapper;
    }

    @Override
    @Transactional
    public void stockOut(StockOutRequest req) {
        // 校验出库类型
        if (req.getOutType() < 1 || req.getOutType() > 3) {
            throw new BizException(400, "出库类型无效，1销售 2损耗 3退货");
        }

        // 销售时零售价必填
        if (req.getOutType() == 1 && req.getRetailPrice() == null) {
            throw new BizException(400, "销售出库时零售价不能为空");
        }

        // 获取批次信息
        StockBatch batch = stockBatchMapper.selectById(req.getBatchId());
        if (batch == null) {
            throw new BizException(404, "批次不存在");
        }
        if (batch.getStatus() == null || batch.getStatus() == 0) {
            throw new BizException(400, "该批次已清空，无法出库");
        }
        if (!batch.getDrugId().equals(req.getDrugId())) {
            throw new BizException(400, "批次与药品不匹配");
        }

        // 校验库存是否充足
        if (batch.getQuantity() < req.getQuantity()) {
            throw new BizException(400,
                    "批次库存不足，当前库存：" + batch.getQuantity() + "，出库数量：" + req.getQuantity());
        }

        // 计算总额：销售时 = quantity * retailPrice，其他类型为 0
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (req.getOutType() == 1) {
            totalAmount = req.getRetailPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        }

        // 从批次获取成本价
        BigDecimal costPrice = batch.getCostPrice() != null ? batch.getCostPrice() : BigDecimal.ZERO;

        // 记录出库单
        Long operatorId = AuthContext.require().userId();

        StockOut stockOut = new StockOut();
        stockOut.setDrugId(req.getDrugId());
        stockOut.setBatchId(req.getBatchId());
        stockOut.setOutType(req.getOutType());
        stockOut.setQuantity(req.getQuantity());
        stockOut.setRetailPrice(req.getRetailPrice());
        stockOut.setCostPrice(costPrice);
        stockOut.setTotalAmount(totalAmount);
        stockOut.setOperatorId(operatorId.intValue());
        stockOut.setRemark(req.getRemark());

        stockOutMapper.insert(stockOut);

        // 扣减批次库存
        int newQuantity = batch.getQuantity() - req.getQuantity();
        stockBatchMapper.updateQuantity(req.getBatchId(), newQuantity);

        // 库存清零时将批次状态置为 0
        if (newQuantity == 0) {
            stockBatchMapper.updateStatus(req.getBatchId(), 0);
            log.info("batch cleared, batchId={}", req.getBatchId());
        }

        log.info("stock out recorded, stockOutId={}, drugId={}, batchId={}, outType={}, qty={}, totalAmount={}",
                stockOut.getId(), req.getDrugId(), req.getBatchId(),
                req.getOutType(), req.getQuantity(), totalAmount);
    }

    @Override
    public PageResult<StockOutVO> listStockOut(Integer drugId, Integer outType,
                                               String startDate, String endDate,
                                               int page, int size) {
        int offset = (page - 1) * size;
        LocalDateTime startTime = (startDate != null && !startDate.isEmpty())
                ? LocalDate.parse(startDate).atStartOfDay() : null;
        LocalDateTime endTime = (endDate != null && !endDate.isEmpty())
                ? LocalDate.parse(endDate).plusDays(1).atStartOfDay() : null;
        List<StockOutVO> list = stockOutMapper.selectList(drugId, outType, startTime, endTime, offset, size);
        long total = stockOutMapper.countList(drugId, outType, startTime, endTime);
        return new PageResult<>(total, list);
    }
}
